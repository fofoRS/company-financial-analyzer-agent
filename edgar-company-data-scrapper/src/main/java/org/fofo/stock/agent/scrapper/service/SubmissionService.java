package org.fofo.stock.agent.scrapper.service;

import org.fofo.stock.agent.scrapper.configuration.EdgarScrapperConfiguration;
import org.fofo.stock.agent.scrapper.connector.edgar.EdgarCompanySubmissions;
import org.fofo.stock.agent.scrapper.connector.edgar.EdgarDataFetcherException;
import org.fofo.stock.agent.scrapper.connector.edgar.EdgarPlatformConnector;
import org.fofo.stock.agent.scrapper.connector.notifier.EntityNotifier;
import org.fofo.stock.agent.scrapper.connector.notifier.SubmissionLoadedEvent;
import org.fofo.stock.agent.scrapper.entity.CompanySubmissionsEntity;
import org.fofo.stock.agent.scrapper.repository.CompanySubmissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SubmissionService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionService.class);

    private final EdgarPlatformConnector edgarPlatformConnector;
    private final CompanySubmissionRepository companySubmissionRepository;
    private final EdgarScrapperConfiguration edgarScrapperConfiguration;
    private final EntityNotifier<CompanySubmissionsEntity> submissionLoadedEntityNotifier;

    public SubmissionService(EdgarPlatformConnector edgarPlatformConnector,
                             CompanySubmissionRepository companySubmissionRepository,
                             EdgarScrapperConfiguration edgarScrapperConfiguration,
                             EntityNotifier<CompanySubmissionsEntity> submissionLoadedEntityNotifier) {
        this.edgarPlatformConnector = edgarPlatformConnector;
        this.companySubmissionRepository = companySubmissionRepository;
        this.edgarScrapperConfiguration = edgarScrapperConfiguration;
        this.submissionLoadedEntityNotifier = submissionLoadedEntityNotifier;
    }

    public void addReportLocationToExistingSubmissionReport(String cik, String accessionNumber,
                                                            String documentLocation, String origin) {
        Assert.notNull(cik, "cik must not be null");
        Assert.notNull(accessionNumber, "accessionNumber must not be null");
        Assert.notNull(documentLocation, "documentLocation must not be null");
        Assert.notNull(origin, "origin must not be null");


        CompanySubmissionsEntity.ReportLocationEntity reportLocationEntity =
                CompanySubmissionsEntity.ReportLocationEntity.builder()
                        .location(documentLocation)
                        .origin(origin).build();

        companySubmissionRepository.updateSubmissionWithStatementReportLocation(
                cik, accessionNumber, reportLocationEntity);
    }

    public void loadSubmissionsForDiscoveredCompany(String cik) {
        boolean existSubmissions = existSubmissionsForCik(cik);
        if (!existSubmissions) {
            try {
                Optional<EdgarCompanySubmissions> edgarCompanySubmissionsOptional =
                        edgarPlatformConnector.getEdgarCompanySubmissions(cik);
                if (edgarCompanySubmissionsOptional.isPresent()) {
                    var edgarCompanySubmissions = edgarCompanySubmissionsOptional.get();
                    Set<CompanySubmissionsEntity.CompanyFilingEntity> filings = edgarCompanySubmissions.getFilings()
                            .stream()
                            .filter(companyFiling -> edgarScrapperConfiguration.getSubmissions().getReports()
                                    .contains(companyFiling.getPrimaryDocDescription()))
                            .map(filing -> CompanySubmissionsEntity.CompanyFilingEntity.builder()
                                    .accessionNumber(filing.getAccessionNumber())
                                    .filingDate(filing.getFilingDate())
                                    .reportDate(filing.getReportDate())
                                    .primaryDocument(filing.getPrimaryDocument())
                                    .primaryDocDescription(filing.getPrimaryDocDescription()).build())
                            .collect(Collectors.toSet());

                    CompanySubmissionsEntity newCompanySubmissionsEntity = CompanySubmissionsEntity.builder()
                            .cik(cik)
                            .filings(filings).build();
                    companySubmissionRepository.insertNewCompanySubmissions(newCompanySubmissionsEntity);
                    submissionLoadedEntityNotifier.notify(newCompanySubmissionsEntity);
                } else {
                    log.info("No company submissions found for cik: {}",  cik);
                }
            } catch (EdgarDataFetcherException e) {
                log.error("Error while fetching company submissions for ticker {}", cik, e);
                throw new RuntimeException(e);
            }
        } else {
            log.info("Submissions already exists for cik: {}", cik);
        }
    }


    public boolean existSubmissionsForCik(String cik) {
        Assert.notNull(cik,"cik must not be null");
        return companySubmissionRepository.existsCompanySubmissions(cik);
    }
}

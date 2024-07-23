package org.fofo.stock.agent.scrapper.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fofo.stock.agent.scrapper.connector.edgar.EdgarDataFetcherException;
import org.fofo.stock.agent.scrapper.connector.edgar.EdgarPlatformConnector;
import org.fofo.stock.agent.scrapper.connector.writer.StatementReportWriterConnector;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
@AllArgsConstructor
public class StatementReportFetcherService {
    private final EdgarPlatformConnector edgarPlatformConnector;
    private final SubmissionService submissionService;
    @Qualifier("localDiskReportWriter")
    private final StatementReportWriterConnector statementReportWriterConnector;

    public void fetchStatementReport(String cik, String accessionNumber, String primaryDocument,
                                     String primaryDocumentDescription) {
        Assert.notNull(cik, "cik must not be null");
        Assert.notNull(accessionNumber, "accessionNumber must not be null");
        Assert.notNull(primaryDocumentDescription, "primaryDocumentDescription must not be null");

        try {
            byte[] documentData = edgarPlatformConnector.getSubmissionStatementReport(
                    cik, accessionNumber, primaryDocument);
            StatementReportWriterConnector.StatementReportWriterRequest writerRequest =
                    StatementReportWriterConnector.StatementReportWriterRequest.builder()
                            .data(documentData)
                            .reportName(primaryDocument)
                            .companyCik(cik)
                            .accessionNumber(accessionNumber).build();
            StatementReportWriterConnector.StatementReportWriterResult writerResult =
                    statementReportWriterConnector.writeReportToDestination(writerRequest);
            submissionService.addReportLocationToExistingSubmissionReport(cik, accessionNumber,
                    writerResult.getLocation(), writerResult.getOriginType());
        } catch (EdgarDataFetcherException e) {
            log.error("Cannot fetch statement report for cik {} and accession number {}", cik, accessionNumber, e);
        }
    }
}

package org.fofo.stock.agent.scrapper.service;

import org.fofo.stock.agent.scrapper.connector.edgar.EdgarCompanyTicker;
import org.fofo.stock.agent.scrapper.connector.edgar.EdgarDataFetcherException;
import org.fofo.stock.agent.scrapper.connector.edgar.EdgarPlatformConnector;
import org.fofo.stock.agent.scrapper.entity.CompanyTickerEntity;
import org.fofo.stock.agent.scrapper.repository.CompanyTickerRepository;
import org.fofo.stock.agent.scrapper.connector.notifier.CompanyCreatedEntityNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EdgarCompanyTickerService {

    private static final Logger log = LoggerFactory.getLogger(EdgarCompanyTickerService.class);

    private final CompanyTickerRepository companyStockDataRepository;
    private final EdgarPlatformConnector edgarPlatformConnector;
    private final CompanyCreatedEntityNotifier companyCreatedNotifier;


    public EdgarCompanyTickerService(CompanyTickerRepository companyStockDataRepository,
                                     EdgarPlatformConnector edgarPlatformConnector,
                                     CompanyCreatedEntityNotifier companyCreatedNotifier) {
        this.companyStockDataRepository = companyStockDataRepository;
        this.edgarPlatformConnector = edgarPlatformConnector;
        this.companyCreatedNotifier = companyCreatedNotifier;
    }

    public void loadAllNonExistingCompanyData() {
        List<EdgarCompanyTicker> edgarCompanyTickerList = null;
        try {
            edgarCompanyTickerList = edgarPlatformConnector.getEdgarCompanyInformation();
        } catch (EdgarDataFetcherException e) {
            throw new RuntimeException(e);
        }
        List<String> ciks = edgarCompanyTickerList.stream()
                .map(EdgarCompanyTicker::getCik)
                .toList();
        List<String> existingCicks = companyStockDataRepository.findExistingCicks(ciks);
        List<CompanyTickerEntity> nonExistingCompanies = edgarCompanyTickerList.stream()
                .filter(edgarCompanyTicker ->
                        !existingCicks.contains(edgarCompanyTicker.getCik()))
                .map(edgarCompany -> new CompanyTickerEntity(edgarCompany.getCompanyName(), edgarCompany.getTicker(),
                        edgarCompany.getCik(), edgarCompany.getStockExchange()))
                .toList();
        if (nonExistingCompanies.isEmpty()) {
            log.debug("List of Companies is empty.");
        } else {
            companyStockDataRepository.saveAllCompanyDataInBatch(nonExistingCompanies);
            companyCreatedNotifier.batchNotify(nonExistingCompanies.toArray(new CompanyTickerEntity[0]));
        }
    }
}

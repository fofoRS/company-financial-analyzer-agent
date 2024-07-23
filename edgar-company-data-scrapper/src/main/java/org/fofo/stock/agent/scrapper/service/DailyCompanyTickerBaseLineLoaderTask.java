package org.fofo.stock.agent.scrapper.service;

import org.fofo.stock.agent.scrapper.connector.edgar.EdgarPlatformConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



@Component
public class DailyCompanyTickerBaseLineLoaderTask {

    private static final Logger log = LoggerFactory.getLogger(DailyCompanyTickerBaseLineLoaderTask.class);

    private final EdgarCompanyTickerService edgarCompanyTickerService;


    public DailyCompanyTickerBaseLineLoaderTask(
            EdgarPlatformConnector edgarPlatformConnector,
            EdgarCompanyTickerService edgarCompanyTickerService) {
        this.edgarCompanyTickerService = edgarCompanyTickerService;
    }

    public void loadEdgarCompanyData() {
        edgarCompanyTickerService.loadAllNonExistingCompanyData();
    }
}

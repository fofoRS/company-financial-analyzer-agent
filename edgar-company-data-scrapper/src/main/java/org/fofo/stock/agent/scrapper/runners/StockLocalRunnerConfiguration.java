package org.fofo.stock.agent.scrapper.runners;

import org.fofo.stock.agent.scrapper.service.DailyCompanyTickerBaseLineLoaderTask;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("COMPANY_LOADER_LOCAL")
@Configuration
public class StockLocalRunnerConfiguration {

    private final DailyCompanyTickerBaseLineLoaderTask baseLineEdgarDataLoaderTask;

    public StockLocalRunnerConfiguration(DailyCompanyTickerBaseLineLoaderTask baseLineEdgarDataLoaderTask) {
        this.baseLineEdgarDataLoaderTask = baseLineEdgarDataLoaderTask;
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return new StockLocalRunnerApplication();
    }

    public class StockLocalRunnerApplication implements ApplicationRunner {
        @Override
        public void run(ApplicationArguments args) throws Exception {
            baseLineEdgarDataLoaderTask.loadEdgarCompanyData();
        }
    }
}

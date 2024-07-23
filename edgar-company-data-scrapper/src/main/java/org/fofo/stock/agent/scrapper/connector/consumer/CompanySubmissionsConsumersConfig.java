package org.fofo.stock.agent.scrapper.connector.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fofo.stock.agent.scrapper.connector.notifier.SubmissionLoadedEvent;
import org.fofo.stock.agent.scrapper.connector.notifier.CompanyCreatedEvent;
import org.fofo.stock.agent.scrapper.service.StatementReportFetcherService;
import org.fofo.stock.agent.scrapper.service.SubmissionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@Slf4j
@AllArgsConstructor
public class CompanySubmissionsConsumersConfig {


    private final SubmissionService submissionService;
    private final StatementReportFetcherService statementReportFetcherService;

    @Bean
    public Consumer<Message<CompanyCreatedEvent>> companySubmissionsCompanyTickerCreated() {
        return eventMessage -> {
            log.info("Event received at companySubmissionsCompanyTickerCreated with payload {}",
                    eventMessage.getPayload());
            submissionService.loadSubmissionsForDiscoveredCompany(eventMessage.getPayload().getData().getCik());
        };
    }

    @Bean
    public Consumer<Message<SubmissionLoadedEvent>> submissionCreated() {
        return eventMessage -> {
            log.info("Event received at  submissionCreated consumer with payload {}", eventMessage.getPayload());
            SubmissionLoadedEvent.SubmissionsLoadedPayload payload  = eventMessage.getPayload().getData();
            statementReportFetcherService.fetchStatementReport(
                    payload.getCik(),payload.getAccessionNumber(),
                    payload.getPrimaryDocument(), payload.getPrimaryDocumentDescription());
        };
    }
}

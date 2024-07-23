package org.fofo.stock.agent.scrapper.connector.notifier;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fofo.stock.agent.scrapper.entity.CompanySubmissionsEntity;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component("companySubmissionCreateNotifier")
@Slf4j
@AllArgsConstructor
public class SubmissionLoadedNotifier implements EntityNotifier<CompanySubmissionsEntity> {

    private final StreamBridge streamBridge;
    private final String OUTPUT_BINDING_CHANNEL = "companySubmissionCreated-out-0";

    @Override
    public void notify(CompanySubmissionsEntity entity) {
        String cik = entity.getCik();
        entity.getFilings().stream()
                .map(filing ->  SubmissionLoadedEvent.SubmissionsLoadedPayload.builder()
                        .cik(cik)
                        .accessionNumber(filing.getAccessionNumber())
                        .primaryDocumentDescription(filing.getPrimaryDocDescription())
                        .primaryDocument(filing.getPrimaryDocument()).build())
                .map(eventPayload -> SubmissionLoadedEvent.builder()
                        .eventType("COMPANY_SUBMISSION_CREATED")
                        .data(eventPayload).build())
                .map(event -> MessageBuilder
                        .withPayload(event)
                        .setHeader("content-type", "application/json").build())
                .forEach(message -> streamBridge.send(OUTPUT_BINDING_CHANNEL,message));
    }
}

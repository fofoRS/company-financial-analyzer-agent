package org.fofo.stock.agent.scrapper.connector.notifier;

import org.fofo.stock.agent.scrapper.entity.CompanyTickerEntity;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component("companyCreatedNotifier")
public class CompanyCreatedEntityNotifier implements EntityNotifier<CompanyTickerEntity> {
    private final StreamBridge streamBridge;
    private static final String OUTPUT_CHANNEL_BINDING = "companyTickerCreated-out-0";

    public CompanyCreatedEntityNotifier(StreamBridge streamBridge) {

        this.streamBridge = streamBridge;
    }

    @Override
    public void notify(CompanyTickerEntity entity) {
        CompanyCreatedEvent eventPayload = createCompanyCreatedEvent(entity);
        Message<CompanyCreatedEvent> message = MessageBuilder.withPayload(eventPayload)
                .setHeader("content-type", "application/json").build();
        streamBridge.send(OUTPUT_CHANNEL_BINDING, message);
    }

    private CompanyCreatedEvent createCompanyCreatedEvent(CompanyTickerEntity entity) {
        return new CompanyCreatedEvent("COMPANY_TICKER_CREATED",entity);
    }
}

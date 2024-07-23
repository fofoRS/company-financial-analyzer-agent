package org.fofo.stock.agent.scrapper.connector.notifier;

import org.fofo.stock.agent.scrapper.entity.CompanyTickerEntity;

public class CompanyCreatedEvent extends Event<CompanyTickerEntity> {

    public CompanyCreatedEvent(String eventType, CompanyTickerEntity data) {
        super(eventType, data);
    }

    public CompanyCreatedEvent() {super();}

}

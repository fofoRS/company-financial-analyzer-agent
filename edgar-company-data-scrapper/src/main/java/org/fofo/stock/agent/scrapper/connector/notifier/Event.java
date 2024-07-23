package org.fofo.stock.agent.scrapper.connector.notifier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Event<T> {
    private LocalDateTime eventTimestamp;
    private String eventType;
    private T data;

    public Event(String eventType, T data) {
        this.eventTimestamp = LocalDateTime.now();
        this.eventType = eventType;
        this.data = data;
    }
}

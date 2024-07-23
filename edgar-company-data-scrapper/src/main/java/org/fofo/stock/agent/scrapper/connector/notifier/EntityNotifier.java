package org.fofo.stock.agent.scrapper.connector.notifier;

import java.util.Arrays;

public interface EntityNotifier<T> {
    void notify(T entity);
    default void batchNotify(T ... entities) {
        Arrays.stream(entities)
                .forEach(this::notify);
    }
}

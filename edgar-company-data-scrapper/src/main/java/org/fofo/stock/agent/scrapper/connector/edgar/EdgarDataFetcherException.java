package org.fofo.stock.agent.scrapper.connector.edgar;

public class EdgarDataFetcherException extends  Exception {
    public EdgarDataFetcherException(String message) {
        super(message);
    }

    public EdgarDataFetcherException(String message, Throwable cause) {
        super(message, cause);
    }
}

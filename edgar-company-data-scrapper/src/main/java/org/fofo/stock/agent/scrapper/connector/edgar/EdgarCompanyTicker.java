package org.fofo.stock.agent.scrapper.connector.edgar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EdgarCompanyTicker {
    @JsonProperty("name")
    private String companyName;
    private String ticker;
    private String cik;
    @JsonProperty("exchange")
    private String stockExchange;

    public EdgarCompanyTicker(String companyName, String ticker, String cik, String stockExchange) {
        this.companyName = companyName;
        this.ticker = ticker;
        this.cik = cik;
        this.stockExchange = stockExchange;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getCik() {
        return cik;
    }

    public void setCik(String cik) {
        this.cik = cik;
    }

    public String getStockExchange() {
        return stockExchange;
    }

    public void setStockExchange(String stockExchange) {
        this.stockExchange = stockExchange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgarCompanyTicker that = (EdgarCompanyTicker) o;
        return Objects.equals(companyName, that.companyName) && Objects.equals(ticker, that.ticker) && Objects.equals(cik, that.cik) && Objects.equals(stockExchange, that.stockExchange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyName, ticker, cik, stockExchange);
    }
}

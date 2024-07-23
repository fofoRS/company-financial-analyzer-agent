package org.fofo.stock.agent.scrapper.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "company_ticker")
public class CompanyTickerEntity {
    @Id
    private String id;
    private String companyName;
    private String ticker;
    private String cik;
    private String stockExchange;

    public CompanyTickerEntity() {}

    public CompanyTickerEntity(String companyName, String ticker, String cik, String stockExchange) {
        this.companyName = companyName;
        this.ticker = ticker;
        this.cik = cik;
        this.stockExchange = stockExchange;
    }

    public CompanyTickerEntity(String id, String companyName, String ticker, String cik, String stockExchange) {
        this.id = id;
        this.companyName = companyName;
        this.ticker = ticker;
        this.cik = cik;
        this.stockExchange = stockExchange;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        CompanyTickerEntity that = (CompanyTickerEntity) o;
        return Objects.equals(cik, that.cik);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cik);
    }
}

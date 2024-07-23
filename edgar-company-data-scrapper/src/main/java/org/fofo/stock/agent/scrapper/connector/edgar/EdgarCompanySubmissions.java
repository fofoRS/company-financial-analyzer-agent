package org.fofo.stock.agent.scrapper.connector.edgar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EdgarCompanySubmissions {
    String cik;
    Set<CompanyFiling> filings;


    public EdgarCompanySubmissions(String cik, Set<CompanyFiling> filings) {
        this.cik = cik;
        this.filings = filings;
    }

    public String getCik() {
        return cik;
    }

    public void setCik(String cik) {
        this.cik = cik;
    }

    public Set<CompanyFiling> getFilings() {
        return filings;
    }

    public void setFilings(Set<CompanyFiling> filings) {
        this.filings = filings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgarCompanySubmissions that = (EdgarCompanySubmissions) o;
        return Objects.equals(cik, that.cik);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cik);
    }

    public static class CompanyFiling {
        private String accessionNumber;
        private LocalDate filingDate;
        private LocalDate reportDate;
        private String primaryDocument;
        private String primaryDocDescription;


        public CompanyFiling(String accessionNumber, LocalDate filingDate,
                             LocalDate reportDate, String primaryDocument, String primaryDocDescription) {
            this.accessionNumber = accessionNumber;
            this.filingDate = filingDate;
            this.reportDate = reportDate;
            this.primaryDocument = primaryDocument;
            this.primaryDocDescription = primaryDocDescription;
        }

        public String getAccessionNumber() {
            return accessionNumber;
        }

        public void setAccessionNumber(String accessionNumber) {
            this.accessionNumber = accessionNumber;
        }

        public LocalDate getFilingDate() {
            return filingDate;
        }

        public void setFilingDate(LocalDate filingDate) {
            this.filingDate = filingDate;
        }

        public LocalDate getReportDate() {
            return reportDate;
        }

        public void setReportDate(LocalDate reportDate) {
            this.reportDate = reportDate;
        }

        public String getPrimaryDocument() {
            return primaryDocument;
        }

        public void setPrimaryDocument(String primaryDocument) {
            this.primaryDocument = primaryDocument;
        }

        public String getPrimaryDocDescription() {
            return primaryDocDescription;
        }

        public void setPrimaryDocDescription(String primaryDocDescription) {
            this.primaryDocDescription = primaryDocDescription;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CompanyFiling that = (CompanyFiling) o;
            return Objects.equals(accessionNumber, that.accessionNumber) && Objects.equals(primaryDocument, that.primaryDocument);
        }

        @Override
        public int hashCode() {
            return Objects.hash(accessionNumber, primaryDocument);
        }
    }
}

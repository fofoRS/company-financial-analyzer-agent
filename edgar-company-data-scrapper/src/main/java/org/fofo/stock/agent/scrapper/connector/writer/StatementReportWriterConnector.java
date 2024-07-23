package org.fofo.stock.agent.scrapper.connector.writer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public interface StatementReportWriterConnector {

    StatementReportWriterResult writeReportToDestination(StatementReportWriterRequest request);

    default String buildFileName(String cik,String accessionNumber,String reportName) {

        return new StringBuilder(cik)
                .append("_")
                .append(accessionNumber)
                .append("_")
                .append(reportName).toString();
    }

    @Builder
    @Getter
    @AllArgsConstructor
    class StatementReportWriterRequest {
        private String accessionNumber;
        private String reportName;
        private byte[] data;
        private String companyCik;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    class StatementReportWriterResult {
        private String location;
        private String documentName;
        private String originType;
    }
}

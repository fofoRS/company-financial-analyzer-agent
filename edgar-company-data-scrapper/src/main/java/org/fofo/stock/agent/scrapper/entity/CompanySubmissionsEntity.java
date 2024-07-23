package org.fofo.stock.agent.scrapper.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Set;

@Document("company_submissions")
@Builder
@Getter
@AllArgsConstructor
public class CompanySubmissionsEntity {
    @Id
    private String id;
    private final String cik;
    private final Set<CompanyFilingEntity> filings;
    private final ReportLocationEntity reportLocation;


    @Builder
    @Getter
    @AllArgsConstructor
    public static class CompanyFilingEntity {
        private String accessionNumber;
        private LocalDate filingDate;
        private LocalDate reportDate;
        private String primaryDocument;
        private String primaryDocDescription;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class ReportLocationEntity {
        private String location;
        private String origin;
    }
}

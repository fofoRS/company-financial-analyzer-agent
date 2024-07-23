package org.fofo.stock.agent.scrapper.repository;

import lombok.extern.slf4j.Slf4j;
import org.fofo.stock.agent.scrapper.entity.CompanySubmissionsEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
@Slf4j
public class CompanySubmissionRepository {

    private final MongoTemplate mongoTemplate;

    public CompanySubmissionRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void updateSubmissionWithStatementReportLocation(
            String cik, String accessionNumber,
            CompanySubmissionsEntity.ReportLocationEntity reportLocationEntity) {
        mongoTemplate.update(CompanySubmissionsEntity.class)
                .matching(where("cik").is(cik))
                .apply(new Update()
                        .filterArray(where("accessionNumber").is(accessionNumber))
                        .set("reportLocation", reportLocationEntity));
    }

    public boolean existsCompanySubmissions(String cik) {
        return mongoTemplate.query(CompanySubmissionsEntity.class)
                .matching(Query.query(where("cik").is(cik))).exists();
    }

    public void updateCompanySubmissions(String cik,
                                         Set<CompanySubmissionsEntity.CompanyFilingEntity> filings) {
        mongoTemplate.update(CompanySubmissionsEntity.class)
                .matching(Query.query(where("cik").is(cik)))
                .apply(new Update().push("filings").each(filings.toArray()));
    }

    public void insertNewCompanySubmissions(CompanySubmissionsEntity companySubmissionsEntity) {
        mongoTemplate.insert(companySubmissionsEntity);
    }
}

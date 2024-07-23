package org.fofo.stock.agent.scrapper.repository;

import org.fofo.stock.agent.scrapper.entity.CompanyTickerEntity;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Component
public class CompanyTickerRepository {

    private final MongoTemplate mongoTemplate;

    public CompanyTickerRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<String> findExistingCicks(List<String> ciks) {
        return mongoTemplate.query(CompanyTickerEntity.class)
                .matching(query(where("cik").in(ciks)))
                .stream()
                .map(CompanyTickerEntity::getCik)
                .toList();
    }

    public void  saveAllCompanyDataInBatch(List<CompanyTickerEntity> companyTickerEntityList) {
        mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, CompanyTickerEntity.class)
                .insert(companyTickerEntityList)
                .execute();
    }
}

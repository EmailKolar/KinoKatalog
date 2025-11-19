package com.example.KinoKatalog.persistence.document.repository.customImpl;

import com.example.KinoKatalog.persistence.document.documents.MovieDocument;
import com.example.KinoKatalog.persistence.document.repository.CompanyRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@RequiredArgsConstructor
public class CompanyRepositoryImpl implements CompanyRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public void updateCompanyNameInAllMovies(ObjectId companyId, String newName) {

        Query query = new Query(
                Criteria.where("productionCompanies.id").is(companyId)
        );

        Update update = new Update()
                .set("productionCompanies.$.name", newName);

        mongoTemplate.updateMulti(query, update, MovieDocument.class);
    }
}

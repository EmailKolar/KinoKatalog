package com.example.KinoKatalog.persistence.document.repository;

import com.example.KinoKatalog.persistence.document.documents.MovieDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MovieDocumentRepository extends MongoRepository<MovieDocument, ObjectId> {
    //TODO: Add custom query methods if needed


    @Override
    List<MovieDocument> findAll();
    List<MovieDocument> findAllByCompanyId(ObjectId id);
}

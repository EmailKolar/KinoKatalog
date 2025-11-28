package com.example.kinokatalog.persistence.document.repository;

import com.example.kinokatalog.persistence.document.documents.MovieDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieDocumentRepository extends MongoRepository<MovieDocument, ObjectId> {
    //TODO: Add custom query methods if needed
}

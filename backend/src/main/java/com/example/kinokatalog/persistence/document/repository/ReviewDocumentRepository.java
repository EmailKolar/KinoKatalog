package com.example.kinokatalog.persistence.document.repository;

import com.example.kinokatalog.persistence.document.documents.ReviewDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewDocumentRepository extends MongoRepository<ReviewDocument, ObjectId> {
}

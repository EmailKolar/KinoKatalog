package com.example.kinokatalog.persistence.document.repository;

import com.example.kinokatalog.persistence.document.documents.PersonDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PersonDocumentRepository extends MongoRepository<PersonDocument, ObjectId> {
}

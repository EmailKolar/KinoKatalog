package com.example.kinokatalog.persistence.document.repository;

import com.example.kinokatalog.persistence.document.documents.CompanyDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompanyDocumentRepository extends MongoRepository<CompanyDocument, ObjectId> {
}

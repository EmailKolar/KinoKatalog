package com.example.kinokatalog.persistence.document.repository;

import com.example.kinokatalog.persistence.document.documents.UserDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserDocumentRepository extends MongoRepository<UserDocument, ObjectId> {
}

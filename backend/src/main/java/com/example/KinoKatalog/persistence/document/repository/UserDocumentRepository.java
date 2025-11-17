package com.example.KinoKatalog.persistence.document.repository;

import com.example.KinoKatalog.persistence.document.documents.UserDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserDocumentRepository extends MongoRepository<UserDocument, ObjectId> {
}

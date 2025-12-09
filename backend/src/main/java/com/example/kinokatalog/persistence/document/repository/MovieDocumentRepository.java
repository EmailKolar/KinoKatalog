package com.example.kinokatalog.persistence.document.repository;

import com.example.kinokatalog.persistence.document.documents.MovieDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MovieDocumentRepository extends MongoRepository<MovieDocument, ObjectId> {
    List<MovieDocument> findByTitle(String title);
    List<MovieDocument> findByTitleContainingIgnoreCase(String title);
    List<MovieDocument> findByGenresContaining(String genre);
}

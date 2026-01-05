package com.example.kinokatalog.service.document;

import com.example.kinokatalog.persistence.document.documents.MovieDocument;
import com.example.kinokatalog.persistence.document.repository.MovieDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "documentTransactionManager")
public class MovieDocumentService {

    private final MovieDocumentRepository repository;


    public MovieDocument getById(ObjectId id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
    }

    public List<MovieDocument> getAll() {
        return repository.findAll();
    }

    public void delete(ObjectId id) {
        repository.deleteById(id);
    }

    public List<MovieDocument> findByTitle(String title) {
        return repository.findByTitleContainingIgnoreCase(title);
    }

    public MovieDocument create(MovieDocument movie) {
        return repository.save(movie);
    }





}

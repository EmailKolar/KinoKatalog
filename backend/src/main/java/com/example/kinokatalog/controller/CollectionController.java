package com.example.kinokatalog.controller;


import com.example.kinokatalog.dto.CollectionDTO;
import com.example.kinokatalog.dto.CreateCollectionDTO;
import com.example.kinokatalog.exception.NotFoundException;
import com.example.kinokatalog.service.CollectionService;
import com.example.kinokatalog.service.impl.CollectionServiceSqlImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {

    @Autowired
    private final CollectionServiceSqlImpl collectionService;

    // CREATE
    @PostMapping
    public ResponseEntity<CollectionDTO> create(@RequestBody CreateCollectionDTO dto) {
        try {
            CollectionDTO created = collectionService.createCollection(
                    dto.getUserId(),
                    dto.getName(),
                    dto.getDescription(),
                    dto.getUsername()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        }
        catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<CollectionDTO> update(@PathVariable Integer id, @RequestBody CollectionDTO dto) {
        try {
            CollectionDTO updated = collectionService.updateCollection(id, dto);
            return ResponseEntity.ok(updated);
        }
        catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<CollectionDTO> get(@PathVariable Integer id) {
        try {
            CollectionDTO col = collectionService.getCollectionById(id);
            return ResponseEntity.ok(col);
        }
        catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET USER COLLECTIONS
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CollectionDTO>> getUserCollections(@PathVariable Integer userId) {
        try {
            List<CollectionDTO> collections = collectionService.getCollectionsByUser(userId);
            return ResponseEntity.ok(collections);
        }
        catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            collectionService.deleteCollection(id);
            return ResponseEntity.noContent().build();
        }
        catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ADD MOVIE
    @PostMapping("/{collectionId}/movies/{movieId}")
    public ResponseEntity<Void> add(@PathVariable Integer collectionId, @PathVariable Integer movieId) {
        try {
            collectionService.addMovieToCollection(collectionId, movieId);
            return ResponseEntity.ok().build();
        }
        catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    // REMOVE MOVIE
    @DeleteMapping("/{collectionId}/movies/{movieId}")
    public ResponseEntity<Void> remove(@PathVariable Integer collectionId, @PathVariable Integer movieId) {
        try {
            collectionService.removeMovieFromCollection(collectionId, movieId);
            return ResponseEntity.noContent().build();
        }
        catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
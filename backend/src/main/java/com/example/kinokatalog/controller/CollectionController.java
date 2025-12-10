package com.example.kinokatalog.controller;


import com.example.kinokatalog.dto.CollectionDTO;
import com.example.kinokatalog.service.CollectionService;
import com.example.kinokatalog.service.impl.CollectionServiceSqlImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {

    @Autowired
    private final CollectionServiceSqlImpl collectionService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CollectionDTO create(@RequestBody CollectionDTO dto) {
        return collectionService.createCollection(dto.getUserId(), dto.getName(), dto.getDescription(), dto.getUsername());
    }

    @PutMapping("/{id}")
    public CollectionDTO update(@PathVariable Integer id, @RequestBody CollectionDTO dto) {
        return collectionService.updateCollection(id, dto);
    }

    @GetMapping("/{id}")
    public CollectionDTO get(@PathVariable Integer id) {
        return collectionService.getCollectionById(id);
    }

    @GetMapping("/user/{userId}")
    public List<CollectionDTO> getUserCollections(@PathVariable Integer userId) {
        return collectionService.getCollectionsByUser(userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        collectionService.deleteCollection(id);
    }

    @PostMapping("/{collectionId}/movies/{movieId}")
    public void add(@PathVariable Integer collectionId, @PathVariable Integer movieId) {
        collectionService.addMovieToCollection(collectionId, movieId);
    }

    @DeleteMapping("/{collectionId}/movies/{movieId}")
    public void remove(@PathVariable Integer collectionId, @PathVariable Integer movieId) {
        collectionService.removeMovieFromCollection(collectionId, movieId);
    }
}

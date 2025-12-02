package com.example.kinokatalog.service;

import com.example.kinokatalog.dto.CollectionDTO;

import java.util.List;


public interface CollectionService {


    CollectionDTO createCollection(CollectionDTO dto);

    CollectionDTO updateCollection(Integer id, CollectionDTO dto);

    void deleteCollection(Integer id);

    CollectionDTO getCollectionById(Integer id);

    List<CollectionDTO> getCollectionsByUser(Integer userId);

    void addMovieToCollection(Integer collectionId, Integer movieId);

    void removeMovieFromCollection(Integer collectionId, Integer movieId);
}

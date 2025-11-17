package com.example.KinoKatalog.service;

import com.example.KinoKatalog.dto.CollectionDTO;

import java.util.List;
import java.util.Optional;


public interface CollectionService {


    CollectionDTO createCollection(CollectionDTO dto);

    CollectionDTO updateCollection(Integer id, CollectionDTO dto);

    void deleteCollection(Integer id);

    CollectionDTO getCollectionById(Integer id);

    List<CollectionDTO> getCollectionsByUser(Integer userId);

    void addMovieToCollection(Integer collectionId, Integer movieId);

    void removeMovieFromCollection(Integer collectionId, Integer movieId);
}

package com.example.kinokatalog.service.impl;

import com.example.kinokatalog.dto.CollectionDTO;
import com.example.kinokatalog.mapper.CollectionMapper;

import com.example.kinokatalog.persistence.sql.entity.MovieEntity;
import com.example.kinokatalog.persistence.sql.entity.UserEntity;
import com.example.kinokatalog.persistence.sql.repository.CollectionMovieSqlRepository;
import com.example.kinokatalog.persistence.sql.repository.MovieSqlRepository;
import com.example.kinokatalog.persistence.sql.repository.UserSqlRepository;
import com.example.kinokatalog.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.kinokatalog.persistence.sql.entity.CollectionEntity;
import com.example.kinokatalog.persistence.sql.entity.CollectionMovieEntity;
import com.example.kinokatalog.persistence.sql.repository.CollectionSqlRepository;

import java.util.*;
@Service
@RequiredArgsConstructor
@Transactional
public class CollectionServiceSqlImpl implements CollectionService {

    private final CollectionSqlRepository collectionRepo;
    private final CollectionMovieSqlRepository collectionMovieRepo;
    private final MovieSqlRepository movieRepo;
    private final UserSqlRepository userRepo;
    private final CollectionMapper mapper;


    @Override
    public CollectionDTO getCollectionById(Integer id) {

        CollectionEntity entity = collectionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        CollectionDTO dto = mapper.toDTO(entity);

        // updated for JPA relation model
        List<Integer> movieIds = collectionMovieRepo.findByCollectionId(id)
                .stream()
                .map(cm -> cm.getMovie().getId())  // <-- FIXED HERE
                .toList();

        dto.setMovieIds(movieIds);

        return dto;
    }


    @Override
    public CollectionDTO createCollection(CollectionDTO dto) {

        if (collectionRepo.existsByUserIdAndName(dto.getUserId(), dto.getName())) {
            throw new IllegalArgumentException("Collection with that name already exists for this user.");
        }

        UserEntity user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CollectionEntity entity = new CollectionEntity();
        entity.setUser(user);                 // <-- FIXED
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());

        CollectionEntity saved = collectionRepo.save(entity);

        return mapper.toDTO(saved);
    }

    @Override
    public CollectionDTO updateCollection(Integer id, CollectionDTO dto) {
        CollectionEntity entity = collectionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());

        return mapper.toDTO(collectionRepo.save(entity));
    }

    @Override
    public void deleteCollection(Integer id) {
        collectionMovieRepo.deleteByCollectionId(id);
        collectionRepo.deleteById(id);
    }

    @Override
    public void addMovieToCollection(Integer collectionId, Integer movieId) {

        if (collectionMovieRepo.existsByCollectionIdAndMovieId(collectionId, movieId)) {
            return;
        }

        CollectionEntity collection = collectionRepo.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        MovieEntity movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        CollectionMovieEntity join = new CollectionMovieEntity();
        join.setCollection(collection);   // <-- FIXED
        join.setMovie(movie);            // <-- FIXED

        collectionMovieRepo.save(join);
    }

    @Override
    public void removeMovieFromCollection(Integer collectionId, Integer movieId) {
        collectionMovieRepo.deleteByCollectionIdAndMovieId(collectionId, movieId);
    }


    @Override
    public List<CollectionDTO> getCollectionsByUser(Integer userId) {
        return collectionRepo.findByUserId(userId).stream()
                .map(mapper::toDTO)
                .toList();
    }
}

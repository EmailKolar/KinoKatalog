package com.example.kinokatalog.service.impl;

import com.example.kinokatalog.dto.CollectionDTO;
import com.example.kinokatalog.exception.ConflictException;
import com.example.kinokatalog.exception.InvalidDataException;
import com.example.kinokatalog.exception.NotFoundException;
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

        if (dto == null) {
            throw new InvalidDataException("Request body cannot be null");
        }

        Integer userId = dto.getUserId();
        String name = dto.getName();
        String description = dto.getDescription();

        if (userId == null || userId < 1) {
            throw new InvalidDataException("Invalid user ID");
        }

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (name == null || name.isBlank()) {
            throw new InvalidDataException("Collection name cannot be empty");
        }

        if (name.length() > 100) {
            throw new InvalidDataException("Collection name too long");
        }

        if (hasUnsafeCharacters(name)) {
            throw new InvalidDataException("Collection name contains illegal characters");
        }

        if (collectionRepo.existsByUserIdAndName(userId, name)) {
            throw new ConflictException("Collection with that name already exists for this user");
        }

        if (description != null) {

            if (description.length() > 4000) {
                throw new InvalidDataException("Description too long");
            }

            if (hasUnsafeCharacters(description)) {
                throw new InvalidDataException("Description contains illegal characters");
            }
        }

        CollectionEntity entity = new CollectionEntity();
        entity.setUser(user);
        entity.setName(name);
        entity.setDescription(description);

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

    private boolean hasUnsafeCharacters(String s) {
        for (char c : s.toCharArray()) {
            if (c <= 31 || c == 127) {
                return true;
            }
        }
        return false;
    }
}

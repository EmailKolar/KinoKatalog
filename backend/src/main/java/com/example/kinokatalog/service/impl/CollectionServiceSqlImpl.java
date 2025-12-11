package com.example.kinokatalog.service.impl;

import com.example.kinokatalog.dto.CollectionDTO;
import com.example.kinokatalog.exception.InvalidDataException;
import com.example.kinokatalog.exception.NotFoundException;
import com.example.kinokatalog.exception.UnauthorizedException;
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
public class CollectionServiceSqlImpl{

    private final CollectionSqlRepository collectionRepo;
    private final CollectionMovieSqlRepository collectionMovieRepo;
    private final MovieSqlRepository movieRepo;
    private final UserSqlRepository userRepo;
    private final CollectionMapper mapper;



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


    public CollectionDTO createCollection(Integer userId, String name, String description, String authenticatedUsername) {

        // 1. Validate userId
        if (userId == null || userId < 1) {
            throw new UnauthorizedException("Invalid userId");
        }

        // 2. Fetch user
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // 3. Authenticated user must match owner
        if (!user.getUsername().equals(authenticatedUsername)) {
            throw new UnauthorizedException("Not allowed to create collection for another user");
        }

        // 4. Validate name (non-null, non-empty, ≤100, safe chars)
        if (name == null || name.isBlank()) {
            throw new InvalidDataException("Name cannot be null or empty");
        }
        if (name.length() > 100) {
            throw new InvalidDataException("Name too long");
        }
        if (containsUnsafeChars(name)) {
            throw new InvalidDataException("Name contains invalid characters");
        }

        // 5. Validate description
        if (description != null) {
            if (description.length() > 4000) {
                throw new InvalidDataException("Description too long");
            }
            if (containsUnsafeChars(description)) {
                throw new InvalidDataException("Description contains invalid characters");
            }
        }

        // 6. Check duplicate
        if (collectionRepo.existsByUserIdAndName(userId, name)) {
            throw new InvalidDataException("Collection name already exists");
        }

        // 7. Create entity
        CollectionEntity entity = new CollectionEntity();
        entity.setUser(user);
        entity.setName(name);
        entity.setDescription(description);

        CollectionEntity saved = collectionRepo.save(entity);
        return mapper.toDTO(saved);
    }

    private boolean containsUnsafeChars(String s) {
        for (char c : s.toCharArray()) {
            if (c <= 31 || c == 127) return true;
        }
        return false;
    }


    public CollectionDTO updateCollection(Integer id, CollectionDTO dto) {
        CollectionEntity entity = collectionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());

        return mapper.toDTO(collectionRepo.save(entity));
    }


    @Transactional("transactionManager")
    public void deleteCollection(Integer id) {
        collectionMovieRepo.deleteByCollectionId(id);
        collectionRepo.deleteById(id);
    }


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


    @Transactional("transactionManager")
    public void removeMovieFromCollection(Integer collectionId, Integer movieId) {
        collectionMovieRepo.deleteByCollectionIdAndMovieId(collectionId, movieId);
    }



    public List<CollectionDTO> getCollectionsByUser(Integer userId) {
        return collectionRepo.findByUserId(userId).stream()
                .map(mapper::toDTO)
                .toList();
    }
}

package com.example.KinoKatalog.service.impl;

import com.example.KinoKatalog.dto.CollectionDTO;
import com.example.KinoKatalog.mapper.CollectionMapper;
import com.example.KinoKatalog.persistance.sql.entity.CollectionEntity;
import com.example.KinoKatalog.persistance.sql.entity.CollectionMovieEntity;
import com.example.KinoKatalog.persistance.sql.entity.MovieEntity;
import com.example.KinoKatalog.persistance.sql.repository.CollectionMovieSqlRepository;
import com.example.KinoKatalog.persistance.sql.repository.CollectionSqlRepository;
import com.example.KinoKatalog.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Profile("sql")
@RequiredArgsConstructor
@Transactional
public class CollectionServiceSqlImpl implements CollectionService {

    private final CollectionSqlRepository collectionRepo;
    private final CollectionMovieSqlRepository collectionMovieRepo;
    private final CollectionMapper mapper;


    @Override
    public CollectionDTO getCollectionById(Integer id) {

        CollectionEntity entity = collectionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        CollectionDTO dto = mapper.toDTO(entity);

        // now fill the movieIds
        List<Integer> movieIds = collectionMovieRepo.findByCollectionId(id)
                .stream()
                .map(CollectionMovieEntity::getMovieId)
                .toList();

        dto.setMovieIds(movieIds);

        return dto;
    }


    @Override
    public CollectionDTO createCollection(CollectionDTO dto) {

        // optional but smart: enforce name uniqueness per user
        if (collectionRepo.existsByUserIdAndName(dto.getUserId(), dto.getName())) {
            throw new IllegalArgumentException("Collection with that name already exists for this user.");
        }

        CollectionEntity entity = new CollectionEntity();
        entity.setUserId(dto.getUserId());
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

        CollectionEntity updated = collectionRepo.save(entity);
        return mapper.toDTO(updated);
    }

    @Override
    public void deleteCollection(Integer id) {
        // delete join table entries
        collectionMovieRepo.deleteByCollectionId(id);

        // delete collection row
        collectionRepo.deleteById(id);
    }

    @Override
    public void addMovieToCollection(Integer collectionId, Integer movieId) {

        if (collectionMovieRepo.existsByCollectionIdAndMovieId(collectionId, movieId)) {
            return; // no need to throw, silently ignore (like Letterboxd)
        }

        CollectionMovieEntity join = new CollectionMovieEntity();
        join.setCollectionId(collectionId);
        join.setMovieId(movieId);

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

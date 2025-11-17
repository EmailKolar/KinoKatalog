package com.example.KinoKatalog.persistance.sql.repository;

import com.example.KinoKatalog.persistance.sql.entity.CollectionMovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionMovieSqlRepository extends JpaRepository<CollectionMovieEntity, Integer> {

    boolean existsByCollectionIdAndMovieId(Integer collectionId, Integer movieId);

    void deleteByCollectionIdAndMovieId(Integer collectionId, Integer movieId);

    List<CollectionMovieEntity> findByCollectionId(Integer collectionId);

    void deleteByCollectionId(Integer collectionId);


}

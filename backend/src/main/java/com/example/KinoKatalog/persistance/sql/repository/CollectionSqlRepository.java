package com.example.KinoKatalog.persistance.sql.repository;

import com.example.KinoKatalog.persistance.sql.entity.CollectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionSqlRepository extends JpaRepository<CollectionEntity, Integer> {



    List<CollectionEntity> findByUserId(Integer userId);
    boolean existsByUserIdAndName( Integer userId, String name);

    }

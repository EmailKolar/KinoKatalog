package com.example.kinokatalog.persistence.sql.repository;

import com.example.kinokatalog.persistence.sql.entity.CollectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionSqlRepository extends JpaRepository<CollectionEntity, Integer> {



    List<CollectionEntity> findByUserId(Integer userId);
    boolean existsByUserIdAndName( Integer userId, String name);

    }

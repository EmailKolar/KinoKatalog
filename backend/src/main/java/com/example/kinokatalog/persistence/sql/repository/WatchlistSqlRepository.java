package com.example.kinokatalog.persistence.sql.repository;


import com.example.kinokatalog.persistence.sql.entity.WatchlistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchlistSqlRepository extends JpaRepository<WatchlistEntity, Integer> {
    List<WatchlistEntity> findByUserId(Integer userId);
}

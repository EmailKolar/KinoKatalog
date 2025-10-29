package com.example.KinoKatalog.persistance.sql.repository;


import com.example.KinoKatalog.persistance.sql.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public interface MovieSqlRepository extends JpaRepository<MovieEntity, Integer> {
    List<MovieEntity> findByTitleContainingIgnoreCase(String title);
}



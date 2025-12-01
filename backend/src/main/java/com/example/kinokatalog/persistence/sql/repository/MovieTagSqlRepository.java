package com.example.kinokatalog.persistence.sql.repository;


import com.example.kinokatalog.persistence.sql.entity.MovieTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieTagSqlRepository extends JpaRepository<MovieTagEntity, Integer> {
}
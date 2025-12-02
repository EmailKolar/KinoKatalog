package com.example.kinokatalog.persistence.sql.repository;

import com.example.kinokatalog.persistence.sql.entity.MovieCastEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieCastSqlRepository extends JpaRepository<MovieCastEntity, Integer> {
}

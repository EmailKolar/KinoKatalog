package com.example.kinokatalog.persistence.sql.repository;

import com.example.kinokatalog.persistence.sql.entity.MovieGenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieGenreSqlRepository extends JpaRepository<MovieGenreEntity, Integer> {
}

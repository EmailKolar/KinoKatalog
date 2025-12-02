package com.example.kinokatalog.persistence.sql.repository;

import com.example.kinokatalog.persistence.sql.entity.CompanyMovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyMovieSqlRepository extends JpaRepository<CompanyMovieEntity, Integer> {
}

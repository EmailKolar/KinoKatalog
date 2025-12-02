package com.example.kinokatalog.persistence.sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.kinokatalog.persistence.sql.entity.GenreEntity;

@Repository
public interface GenreSqlRepository extends JpaRepository<GenreEntity, Integer> {


}

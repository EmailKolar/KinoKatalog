package com.example.KinoKatalog.persistence.sql.repository;

import com.example.KinoKatalog.persistence.sql.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagSqlRepository extends JpaRepository<TagEntity, Integer> {

}

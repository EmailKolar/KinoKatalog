package com.example.KinoKatalog.persistence.sql.repository;

import com.example.KinoKatalog.persistence.sql.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonSqlRepository extends JpaRepository<PersonEntity, Integer> {
}

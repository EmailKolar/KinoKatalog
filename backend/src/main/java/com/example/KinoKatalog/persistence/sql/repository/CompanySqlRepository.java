package com.example.KinoKatalog.persistence.sql.repository;


import com.example.KinoKatalog.persistence.sql.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanySqlRepository extends JpaRepository<CompanyEntity, Integer> {
}

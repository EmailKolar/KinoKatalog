package com.example.kinokatalog.persistence.sql.repository;


import com.example.kinokatalog.persistence.sql.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanySqlRepository extends JpaRepository<CompanyEntity, Integer> {
}

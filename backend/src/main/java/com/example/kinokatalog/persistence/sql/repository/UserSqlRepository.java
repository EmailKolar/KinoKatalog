package com.example.kinokatalog.persistence.sql.repository;

import com.example.kinokatalog.persistence.sql.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSqlRepository extends JpaRepository<UserEntity, Integer> {
}

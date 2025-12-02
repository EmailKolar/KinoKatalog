package com.example.kinokatalog.persistence.sql.repository;

import com.example.kinokatalog.persistence.sql.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentSqlRepository extends JpaRepository<CommentEntity, Integer> {
}

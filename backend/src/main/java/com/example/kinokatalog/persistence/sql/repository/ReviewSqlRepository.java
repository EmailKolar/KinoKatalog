package com.example.kinokatalog.persistence.sql.repository;


import com.example.kinokatalog.persistence.sql.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public interface ReviewSqlRepository extends JpaRepository<ReviewEntity, Integer> {
    List<ReviewEntity> findByMovieEntity_Id(Integer movieId);

}



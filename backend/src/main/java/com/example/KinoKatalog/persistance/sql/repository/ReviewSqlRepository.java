package com.example.KinoKatalog.persistance.sql.repository;


import com.example.KinoKatalog.persistance.sql.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public interface ReviewSqlRepository extends JpaRepository<ReviewEntity, Integer> {
    List<ReviewEntity> findByMovieEntity_Id(Integer movieId);

}



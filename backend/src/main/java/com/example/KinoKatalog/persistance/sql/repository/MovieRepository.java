package com.example.KinoKatalog.persistance.sql.repository;


import com.example.KinoKatalog.persistance.sql.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    List<Movie> findByTitleContainingIgnoreCase(String title);
}



package com.example.KinoKatalog.service;

import com.example.KinoKatalog.persistence.sql.entity.ReviewEntity;

import java.util.List;

public interface ReviewService {


    List<ReviewEntity> getReviewsByMovie(Integer movieId);
    ReviewEntity addReview(ReviewEntity reviewEntity);
}

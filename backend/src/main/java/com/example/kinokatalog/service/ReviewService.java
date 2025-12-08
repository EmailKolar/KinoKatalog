package com.example.kinokatalog.service;

import com.example.kinokatalog.persistence.sql.entity.ReviewEntity;

import java.util.List;

public interface ReviewService {


    List<ReviewEntity> getReviewsByMovie(Integer movieId);
    ReviewEntity addReview(ReviewEntity reviewEntity);

    ReviewEntity addReviewToMovie(Integer movieId, ReviewEntity review, String username);
}

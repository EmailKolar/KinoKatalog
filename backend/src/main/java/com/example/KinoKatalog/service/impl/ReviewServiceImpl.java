package com.example.KinoKatalog.service.impl;


import com.example.KinoKatalog.persistance.sql.entity.ReviewEntity;
import com.example.KinoKatalog.persistance.sql.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ReviewServiceImpl {
    private final ReviewRepository reviewRepository;

    public List<ReviewEntity> getReviewsByMovie(Integer movieId) {
        return reviewRepository.findByMovieId(movieId);
    }

    public ReviewEntity addReview(ReviewEntity reviewEntity) {
        return reviewRepository.save(reviewEntity);
    }
}


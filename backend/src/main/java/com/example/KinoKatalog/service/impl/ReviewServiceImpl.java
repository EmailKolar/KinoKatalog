package com.example.KinoKatalog.service.impl;


import com.example.KinoKatalog.persistance.sql.entity.ReviewEntity;
import com.example.KinoKatalog.persistance.sql.repository.ReviewRepository;
import com.example.KinoKatalog.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;

    public List<ReviewEntity> getReviewsByMovie(Integer movieId) {
        return reviewRepository.findByMovieId(movieId);
    }

    public ReviewEntity addReview(ReviewEntity reviewEntity) {
        return reviewRepository.save(reviewEntity);
    }
}


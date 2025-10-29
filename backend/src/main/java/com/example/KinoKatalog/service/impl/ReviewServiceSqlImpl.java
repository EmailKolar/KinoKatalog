package com.example.KinoKatalog.service.impl;


import com.example.KinoKatalog.persistance.sql.entity.ReviewEntity;
import com.example.KinoKatalog.persistance.sql.repository.ReviewSqlRepository;
import com.example.KinoKatalog.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ReviewServiceSqlImpl implements ReviewService {
    private final ReviewSqlRepository reviewSqlRepository;

    public List<ReviewEntity> getReviewsByMovie(Integer movieId) {
        return reviewSqlRepository.findByMovieEntity_Id(movieId);
    }

    public ReviewEntity addReview(ReviewEntity reviewEntity) {
        return reviewSqlRepository.save(reviewEntity);
    }
}


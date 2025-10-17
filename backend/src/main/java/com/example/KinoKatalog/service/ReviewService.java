package com.example.KinoKatalog.service;


import com.example.KinoKatalog.persistance.sql.entity.Review;
import com.example.KinoKatalog.persistance.sql.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public List<Review> getReviewsByMovie(Integer movieId) {
        return reviewRepository.findByMovieId(movieId);
    }

    public Review addReview(Review review) {
        return reviewRepository.save(review);
    }
}


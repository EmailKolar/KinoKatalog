package com.example.kinokatalog.controller;


import com.example.kinokatalog.persistence.sql.entity.ReviewEntity;
import com.example.kinokatalog.service.impl.ReviewServiceSqlImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewServiceSqlImpl reviewServiceSqlImpl;

    @GetMapping("/movie/{movieId}")
    public List<ReviewEntity> getReviewsByMovie(@PathVariable Integer movieId) {
        return reviewServiceSqlImpl.getReviewsByMovie(movieId);
    }

    @PostMapping
    public ResponseEntity<ReviewEntity> createReview(@RequestBody ReviewEntity reviewEntity) {
        ReviewEntity saved = reviewServiceSqlImpl.addReview(reviewEntity);
        return ResponseEntity.ok(saved);
    }

}

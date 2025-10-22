package com.example.KinoKatalog.controller;


import com.example.KinoKatalog.persistance.sql.entity.ReviewEntity;
import com.example.KinoKatalog.service.impl.ReviewServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewServiceImpl reviewServiceImpl;

    @GetMapping("/movie/{movieId}")
    public List<ReviewEntity> getReviewsByMovie(@PathVariable Integer movieId) {
        return reviewServiceImpl.getReviewsByMovie(movieId);
    }

    @PostMapping
    public ResponseEntity<ReviewEntity> createReview(@RequestBody ReviewEntity reviewEntity) {
        ReviewEntity saved = reviewServiceImpl.addReview(reviewEntity);
        return ResponseEntity.ok(saved);
    }
}

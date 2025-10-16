package com.example.KinoKatalog.controller;

import com.example.KinoKatalog.model.Review;
import com.example.KinoKatalog.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/movie/{movieId}")
    public List<Review> getReviewsByMovie(@PathVariable Integer movieId) {
        return reviewService.getReviewsByMovie(movieId);
    }

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        Review saved = reviewService.addReview(review);
        return ResponseEntity.ok(saved);
    }
}

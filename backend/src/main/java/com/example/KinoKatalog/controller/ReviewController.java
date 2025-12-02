package com.example.KinoKatalog.controller;


import com.example.KinoKatalog.dto.ReviewDTO;
import com.example.KinoKatalog.mapper.ReviewMapper;
import com.example.KinoKatalog.persistence.sql.entity.ReviewEntity;
import com.example.KinoKatalog.service.impl.ReviewServiceSqlImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewServiceSqlImpl reviewServiceSqlImpl;

    @GetMapping("/movie/{movieId}")
    public List<ReviewDTO> getReviewsByMovie(@PathVariable Integer movieId) {
        return reviewServiceSqlImpl.getReviewsByMovie(movieId).stream()
                              .map(ReviewMapper::toDTO)
                               .collect(Collectors.toList());
    }

//    @PostMapping
//    public ResponseEntity<ReviewEntity> createReview(@RequestBody ReviewEntity reviewEntity) {
//        ReviewEntity saved = reviewServiceSqlImpl.addReview(reviewEntity);
//        return ResponseEntity.ok(saved);
//    }
    // REMOVE or comment out any endpoint that accepts a full ReviewEntity from the client:
    // public ResponseEntity<ReviewEntity> createReview(@RequestBody ReviewEntity reviewEntity) { ... }

    @PostMapping(path = "/movie/{movieId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewDTO> createReviewForMovie(
            @PathVariable Integer movieId,
            @RequestBody Map<String, Object> body,
            Authentication authentication) {

        // require authentication explicitly
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // validate and extract primitive fields only
        Object r = body.get("rating");
        Integer rating = (r instanceof Number) ? ((Number) r).intValue() : null;
        String reviewText = body.get("reviewText") != null ? String.valueOf(body.get("reviewText")).trim() : null;

        if (rating == null || reviewText == null || reviewText.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // build entity with only primitive fields (do not set relations from client input)
        ReviewEntity review = new ReviewEntity();
        review.setRating(rating);
        review.setReviewText(reviewText);

        // pass authenticated username to service so it can attach the user relation
        String username = authentication.getName();
        ReviewEntity saved = reviewServiceSqlImpl.addReviewToMovie(movieId, review, username);

        return ResponseEntity.status(HttpStatus.CREATED).body(ReviewMapper.toDTO(saved));
    }

}

package com.example.kinokatalog.controller;


import com.example.kinokatalog.dto.CreateReviewRequest;
import com.example.kinokatalog.dto.ReviewDTO;
import com.example.kinokatalog.exception.InvalidDataException;
import com.example.kinokatalog.exception.NotFoundException;
import com.example.kinokatalog.exception.UnauthorizedException;
import com.example.kinokatalog.mapper.ReviewMapper;
import com.example.kinokatalog.persistence.sql.entity.MovieEntity;
import com.example.kinokatalog.persistence.sql.entity.ReviewEntity;
import com.example.kinokatalog.service.impl.ReviewServiceSqlImpl;
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

    @PostMapping(path = "/movie/{movieId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewDTO> createReviewForMovie(
            @PathVariable Integer movieId,
            @RequestBody CreateReviewRequest req) {
        try {
            ReviewEntity saved = reviewServiceSqlImpl.addReviewToMovie(
                    movieId,
                    req.getRating(),
                    req.getReviewText(),
                    req.getUsername()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ReviewMapper.toDTO(saved));

        } catch (InvalidDataException ex) {
            return ResponseEntity.badRequest().build();
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


}

package com.example.kinokatalog.service.impl;


import com.example.kinokatalog.exception.InvalidDataException;
import com.example.kinokatalog.exception.NotFoundException;
import com.example.kinokatalog.exception.UnauthorizedException;
import com.example.kinokatalog.persistence.sql.entity.MovieEntity;
import com.example.kinokatalog.persistence.sql.entity.ReviewEntity;
import com.example.kinokatalog.persistence.sql.entity.UserEntity;
import com.example.kinokatalog.persistence.sql.repository.MovieSqlRepository;
import com.example.kinokatalog.persistence.sql.repository.ReviewSqlRepository;

import com.example.kinokatalog.persistence.sql.repository.UserSqlRepository;
import com.example.kinokatalog.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ReviewServiceSqlImpl {
    private final ReviewSqlRepository reviewSqlRepository;
    private final MovieSqlRepository movieSqlRepository;
    private final UserSqlRepository userSqlRepository;

    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public List<ReviewEntity> getReviewsByMovie(Integer movieId) {
        return reviewSqlRepository.findByMovieEntity_Id(movieId);
    }


    @Transactional(transactionManager = "transactionManager", isolation = Isolation.SERIALIZABLE)
    public ReviewEntity addReviewToMovie(
            Integer movieId,
            Integer rating,
            String reviewText,
            String username) {

        if (rating == null || rating < 0 || rating > 10)
            throw new InvalidDataException("Invalid rating");

        if (reviewText == null || reviewText.isBlank())
            throw new InvalidDataException("Invalid text");

        if (reviewText.length() > 5000)
            throw new InvalidDataException("Text too long");
        if (hasUnsafeCharacters(reviewText)) {
            throw new InvalidDataException("Text contains illegal characters");
        }


        UserEntity user = userSqlRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        MovieEntity movie = movieSqlRepository.findById(movieId)
                .orElseThrow(() -> new NotFoundException("Movie not found"));

        ReviewEntity entity = new ReviewEntity();
        entity.setUserEntity(user);
        entity.setMovieEntity(movie);
        entity.setRating(rating);
        entity.setReviewText(reviewText);

        return reviewSqlRepository.save(entity);
    }
    private boolean hasUnsafeCharacters(String s) {
        for (char c : s.toCharArray()) {
            if (c <= 31 || c == 127) {
                return true;
            }
        }
        return false;
    }

}


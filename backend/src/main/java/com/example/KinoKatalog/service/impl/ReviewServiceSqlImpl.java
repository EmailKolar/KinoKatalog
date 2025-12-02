package com.example.KinoKatalog.service.impl;


import com.example.KinoKatalog.persistence.sql.entity.MovieEntity;
import com.example.KinoKatalog.persistence.sql.entity.ReviewEntity;
import com.example.KinoKatalog.persistence.sql.entity.UserEntity;
import com.example.KinoKatalog.persistence.sql.repository.MovieSqlRepository;
import com.example.KinoKatalog.persistence.sql.repository.ReviewSqlRepository;
import com.example.KinoKatalog.persistence.sql.repository.UserSqlRepository;
import com.example.KinoKatalog.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ReviewServiceSqlImpl implements ReviewService {
    private final ReviewSqlRepository reviewSqlRepository;
    private final MovieSqlRepository movieSqlRepository;
    private final UserSqlRepository userSqlRepository;

    public List<ReviewEntity> getReviewsByMovie(Integer movieId) {
        return reviewSqlRepository.findByMovieEntity_Id(movieId);
    }

    public ReviewEntity addReview(ReviewEntity reviewEntity) {
        return reviewSqlRepository.save(reviewEntity);
    }

    /**
     * Attach movie and user (by username) then save.
     * Throws EntityNotFoundException if movie or user is missing.
     */
    @Override
    public ReviewEntity addReviewToMovie(Integer movieId, ReviewEntity reviewEntity, String username) {
        MovieEntity movieEntity = movieSqlRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with id: " + movieId));
        reviewEntity.setMovieEntity(movieEntity);

        if (username == null) {
            throw new EntityNotFoundException("Authenticated user required to add review");
        }

        UserEntity userEntity = userSqlRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        reviewEntity.setUserEntity(userEntity);

        return reviewSqlRepository.save(reviewEntity);
    }
}


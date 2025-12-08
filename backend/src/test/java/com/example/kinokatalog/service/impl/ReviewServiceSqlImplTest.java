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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceSqlImplTest {

    @Mock private UserSqlRepository userRepo;
    @Mock private MovieSqlRepository movieRepo;
    @Mock private ReviewSqlRepository reviewRepo;

    @InjectMocks private ReviewServiceSqlImpl service;

    private UserEntity user;
    private MovieEntity movie;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = new UserEntity();
        user.setId(1);
        user.setUsername("alice");

        movie = new MovieEntity();
        movie.setId(10);
    }

    ReviewEntity savedReview() {
        ReviewEntity r = new ReviewEntity();
        r.setId(99);
        return r;
    }

    @Test
    void validReview_success() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        when(reviewRepo.save(any())).thenReturn(savedReview());

        ReviewEntity result = service.addReviewToMovie(10, 8, "Great movie!", "alice");

        assertEquals(99, result.getId());
    }

    @Test
    void userNotFound_unauthorized() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class,
                () -> service.addReviewToMovie(10, 8, "Great", "alice"));
    }

    @Test
    void movieNotFound_notFound() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.addReviewToMovie(10, 8, "Great", "alice"));
    }

    @Test
    void ratingBelowMin_invalid() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        assertThrows(InvalidDataException.class,
                () -> service.addReviewToMovie(10, -1, "Good", "alice"));
    }

    @Test
    void ratingMinBoundary0_valid() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        when(reviewRepo.save(any())).thenReturn(savedReview());
        assertDoesNotThrow(() ->
                service.addReviewToMovie(10, 0, "Ok", "alice"));
    }

    @Test
    void rating1_valid() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        when(reviewRepo.save(any())).thenReturn(savedReview());
        assertDoesNotThrow(() ->
                service.addReviewToMovie(10, 1, "Fine", "alice"));
    }

    @Test
    void rating10_max_valid() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        when(reviewRepo.save(any())).thenReturn(savedReview());
        assertDoesNotThrow(() ->
                service.addReviewToMovie(10, 10, "Perfect!", "alice"));
    }

    @Test
    void ratingAboveMax_invalid() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        assertThrows(InvalidDataException.class,
                () -> service.addReviewToMovie(10, 11, "Bad", "alice"));
    }

    @Test
    void reviewTextNull_invalid() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        assertThrows(InvalidDataException.class,
                () -> service.addReviewToMovie(10, 5, null, "alice"));
    }

    @Test
    void reviewTextEmpty_invalid() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        assertThrows(InvalidDataException.class,
                () -> service.addReviewToMovie(10, 5, "", "alice"));
    }

    @Test
    void reviewTextLength1_valid() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        when(reviewRepo.save(any())).thenReturn(savedReview());
        assertDoesNotThrow(() ->
                service.addReviewToMovie(10, 5, "a", "alice"));
    }

    @Test
    void reviewTextLength4999_valid() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        when(reviewRepo.save(any())).thenReturn(savedReview());
        assertDoesNotThrow(() ->
                service.addReviewToMovie(10, 5, "a".repeat(4999), "alice"));
    }

    @Test
    void reviewTextLength5000_valid() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        when(reviewRepo.save(any())).thenReturn(savedReview());
        assertDoesNotThrow(() ->
                service.addReviewToMovie(10, 5, "a".repeat(5000), "alice"));
    }

    @Test
    void reviewTextLength5001_invalid() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        assertThrows(InvalidDataException.class,
                () -> service.addReviewToMovie(10, 5, "a".repeat(5001), "alice"));
    }

    @Test
    void invalidTextCharacters_controlChar_invalid() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        String text = "Nice" + '\u0001';
        assertThrows(InvalidDataException.class,
                () -> service.addReviewToMovie(10, 5, text, "alice"));
    }

    @Test
    void decisionTable_userFails_allElseIgnored() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class,
                () -> service.addReviewToMovie(10, 5, "good", "alice"));
    }

    @Test
    void decisionTable_movieFails_afterUserPasses() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.addReviewToMovie(10, 5, "good", "alice"));
    }

    @Test
    void decisionTable_ratingFails_afterUserMoviePass() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        assertThrows(InvalidDataException.class,
                () -> service.addReviewToMovie(10, -1, "good", "alice"));
    }

    @Test
    void decisionTable_textFails_last() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        assertThrows(InvalidDataException.class,
                () -> service.addReviewToMovie(10, 5, "", "alice"));
    }
}

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import org.mockito.*;

import java.util.Optional;
import java.util.stream.Stream;

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


    // --------------------------------------------------
    // VALID review test
    // --------------------------------------------------
    @Test
    void validReview_success() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        when(reviewRepo.save(any())).thenReturn(savedReview());

        assertDoesNotThrow(() ->
                service.addReviewToMovie(10, 8, "Great!", "alice"));
    }


    // --------------------------------------------------
    // PARAMETERIZED Rating tests (EP + BVA)
    // --------------------------------------------------
    static Stream<Integer> validRatings() {
        return Stream.of(
                0,          // min
                1,          // small positive
                10          // max
        );
    }

    @ParameterizedTest
    @MethodSource("validRatings")
    void rating_validValues_success(Integer rating) {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        when(reviewRepo.save(any())).thenReturn(savedReview());

        assertDoesNotThrow(() ->
                service.addReviewToMovie(10, rating, "Good", "alice"));
    }


    static Stream<Integer> invalidRatings() {
        return Stream.of(
                -1,     // below min
                11      // above max
        );
    }

    @ParameterizedTest
    @MethodSource("invalidRatings")
    void rating_invalidValues_throw(Integer rating) {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));

        assertThrows(InvalidDataException.class,
                () -> service.addReviewToMovie(10, rating, "Good", "alice"));
    }


    // --------------------------------------------------
    // PARAMETERIZED Text Tests (EP + BVA)
    // --------------------------------------------------
    static Stream<String> validTextProvider() {
        return Stream.of(
                "a",                    // length 1
                "a".repeat(4999),       // large valid
                "a".repeat(5000)        // max length
        );
    }

    @ParameterizedTest
    @MethodSource("validTextProvider")
    void reviewText_valid(String text) {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        when(reviewRepo.save(any())).thenReturn(savedReview());

        assertDoesNotThrow(() ->
                service.addReviewToMovie(10, 5, text, "alice"));
    }


    static Stream<Arguments> invalidTextProvider() {
        return Stream.of(
                Arguments.of(null, "Null"),
                Arguments.of("", "Empty"),
                Arguments.of("a".repeat(5001), "Too long"),
                Arguments.of("Nice" + '\u0001', "Control char")
        );
    }

    @ParameterizedTest(name = "Invalid text case: {1}")
    @MethodSource("invalidTextProvider")
    void reviewText_invalid(String text, String label) {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));

        assertThrows(InvalidDataException.class,
                () -> service.addReviewToMovie(10, 5, text, "alice"));
    }


    // --------------------------------------------------
    // NON-PARAMETERIZED: User/Movie EP + Decision Table
    // --------------------------------------------------
    @Test
    void userNotFound_unauthorized() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class,
                () -> service.addReviewToMovie(10, 5, "Hi", "alice"));
    }

    @Test
    void movieNotFound_notFound() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.addReviewToMovie(10, 5, "Hi", "alice"));
    }

    @Test
    void decision_userFails_first() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class,
                () -> service.addReviewToMovie(10, 5, "Good", "alice"));
    }

    @Test
    void decision_movieFails_second() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.addReviewToMovie(10, 5, "Good", "alice"));
    }

    @Test
    void decision_ratingFails_third() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        assertThrows(InvalidDataException.class,
                () -> service.addReviewToMovie(10, -1, "Good", "alice"));
    }

    @Test
    void decision_textFails_last() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(movieRepo.findById(10)).thenReturn(Optional.of(movie));
        assertThrows(InvalidDataException.class,
                () -> service.addReviewToMovie(10, 5, "", "alice"));
    }
}

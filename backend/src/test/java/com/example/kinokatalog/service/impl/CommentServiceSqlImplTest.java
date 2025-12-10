package com.example.kinokatalog.service.impl;

import com.example.kinokatalog.exception.InvalidDataException;
import com.example.kinokatalog.exception.NotFoundException;
import com.example.kinokatalog.exception.UnauthorizedException;
import com.example.kinokatalog.persistence.sql.entity.CommentEntity;
import com.example.kinokatalog.persistence.sql.entity.ReviewEntity;
import com.example.kinokatalog.persistence.sql.entity.UserEntity;
import com.example.kinokatalog.persistence.sql.repository.CommentSqlRepository;
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

class CommentServiceSqlImplTest {

    @Mock private UserSqlRepository userRepo;
    @Mock private ReviewSqlRepository reviewRepo;
    @Mock private CommentSqlRepository commentRepo;

    @InjectMocks private CommentServiceSqlImpl service;

    private UserEntity user;
    private ReviewEntity review;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = new UserEntity();
        user.setId(1);
        user.setUsername("alice");

        review = new ReviewEntity();
        review.setId(100);
    }

    CommentEntity savedComment() {
        CommentEntity c = new CommentEntity();
        c.setId(999);
        return c;
    }


    // Parameterized VALID text tests

    static Stream<String> validTextProvider() {
        return Stream.of(
                "a",
                "a".repeat(1999),
                "a".repeat(2000)
        );
    }

    @ParameterizedTest
    @MethodSource("validTextProvider")
    void validCommentTexts_success(String text) {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(reviewRepo.findById(100)).thenReturn(Optional.of(review));
        when(commentRepo.save(any())).thenReturn(savedComment());

        assertDoesNotThrow(() ->
                service.addCommentToReview(100, text, "alice"));
    }

    // Parameterized INVALID text tests (EP + BVA)

    static Stream<Arguments> invalidTextProvider() {
        return Stream.of(
                Arguments.of("", "Empty"),
                Arguments.of(null, "Null"),
                Arguments.of("a".repeat(2001), "Too long"),
                Arguments.of("bad" + '\u0001', "Control char")
        );
    }

    @ParameterizedTest(name = "{1} text should be invalid")
    @MethodSource("invalidTextProvider")
    void invalidTexts_throwException(String text, String caseName) {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(reviewRepo.findById(100)).thenReturn(Optional.of(review));

        assertThrows(InvalidDataException.class,
                () -> service.addCommentToReview(100, text, "alice"));
    }

    // NON-parameterized tests (logic ordering)

    @Test
    void userNotFound_unauthorized() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class,
                () -> service.addCommentToReview(100, "Valid comment", "alice"));
    }

    @Test
    void reviewNotFound_notFound() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(reviewRepo.findById(100)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.addCommentToReview(100, "Valid comment", "alice"));
    }

    @Test
    void decision_userFails_first() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class,
                () -> service.addCommentToReview(100, "text", "alice"));
    }

    @Test
    void decision_reviewFails_second() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(reviewRepo.findById(100)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.addCommentToReview(100, "text", "alice"));
    }

    @Test
    void decision_textFails_last() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(reviewRepo.findById(100)).thenReturn(Optional.of(review));
        assertThrows(InvalidDataException.class,
                () -> service.addCommentToReview(100, "", "alice"));
    }
}

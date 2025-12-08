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
import org.mockito.*;

import java.util.Optional;

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

    @Test
    void validComment_success() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(reviewRepo.findById(100)).thenReturn(Optional.of(review));
        when(commentRepo.save(any())).thenReturn(savedComment());

        CommentEntity result = service.addCommentToReview(100, "Nice review!", "alice");

        assertEquals(999, result.getId());
    }

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
    void emptyCommentText_invalid() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(reviewRepo.findById(100)).thenReturn(Optional.of(review));
        assertThrows(InvalidDataException.class,
                () -> service.addCommentToReview(100, "", "alice"));
    }

    @Test
    void nullCommentText_invalid() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(reviewRepo.findById(100)).thenReturn(Optional.of(review));
        assertThrows(InvalidDataException.class,
                () -> service.addCommentToReview(100, null, "alice"));
    }

    @Test
    void textLength1_valid() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(reviewRepo.findById(100)).thenReturn(Optional.of(review));
        when(commentRepo.save(any())).thenReturn(savedComment());
        assertDoesNotThrow(() ->
                service.addCommentToReview(100, "a", "alice"));
    }

    @Test
    void textLength1999_valid() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(reviewRepo.findById(100)).thenReturn(Optional.of(review));
        when(commentRepo.save(any())).thenReturn(savedComment());
        assertDoesNotThrow(() ->
                service.addCommentToReview(100, "a".repeat(1999), "alice"));
    }

    @Test
    void textLength2000_valid() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(reviewRepo.findById(100)).thenReturn(Optional.of(review));
        when(commentRepo.save(any())).thenReturn(savedComment());
        assertDoesNotThrow(() ->
                service.addCommentToReview(100, "a".repeat(2000), "alice"));
    }

    @Test
    void textLength2001_invalid() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(reviewRepo.findById(100)).thenReturn(Optional.of(review));
        assertThrows(InvalidDataException.class,
                () -> service.addCommentToReview(100, "a".repeat(2001), "alice"));
    }

    @Test
    void unsafeCharacters_invalid() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(reviewRepo.findById(100)).thenReturn(Optional.of(review));
        String text = "Nice" + '\u0001';
        assertThrows(InvalidDataException.class,
                () -> service.addCommentToReview(100, text, "alice"));
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

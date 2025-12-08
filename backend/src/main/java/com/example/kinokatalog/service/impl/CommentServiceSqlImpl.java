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
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceSqlImpl {

    private final UserSqlRepository userRepo;
    private final ReviewSqlRepository reviewRepo;
    private final CommentSqlRepository commentRepo;

    public CommentEntity addCommentToReview(Integer reviewId, String text, String username){
        // 1. Validate user exists
        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        // 2. Validate review exists
        ReviewEntity review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found"));

        // 3. Validate text
        if (text == null || text.isBlank()) {
            throw new InvalidDataException("Comment text cannot be empty");
        }

        if (text.length() > 2000) {
            throw new InvalidDataException("Comment text too long");
        }

        if (hasUnsafeCharacters(text)) {
            throw new InvalidDataException("Comment text contains illegal characters");
        }

        // 4. Create the comment entity
        CommentEntity comment = new CommentEntity();
        comment.setUserEntity(user);
        comment.setReviewEntity(review);
        comment.setCommentText(text);

        // created_at is automatically set by @PrePersist

        // 5. Save and return
        return commentRepo.save(comment);


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

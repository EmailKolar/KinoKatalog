package com.example.KinoKatalog.mapper;


import com.example.KinoKatalog.dto.ReviewDTO;
import com.example.KinoKatalog.persistence.sql.entity.ReviewEntity;

public class ReviewMapper {


    public static ReviewDTO toDTO(ReviewEntity reviewEntity) {
        if (reviewEntity == null) {
            return null;
        }
        ReviewDTO dto = new ReviewDTO();
        dto.setId(reviewEntity.getId());
        dto.setUsername(reviewEntity.getUserEntity().getUsername());
        dto.setMovieId(reviewEntity.getMovieEntity().getId());
        dto.setRating(reviewEntity.getRating());
        dto.setReviewText(reviewEntity.getReviewText());
        dto.setCreatedAt(reviewEntity.getCreatedAt());
        return dto;
    }
    public static ReviewEntity toEntity(ReviewDTO dto) {
        if (dto == null) {
            return null;
        }
        ReviewEntity reviewEntity = new ReviewEntity();
        reviewEntity.setId(dto.getId());
        // Note: User and Movie entities should be set separately
        reviewEntity.setRating(dto.getRating());
        reviewEntity.setReviewText(dto.getReviewText());
        reviewEntity.setCreatedAt(dto.getCreatedAt());
        return reviewEntity;
    }
}

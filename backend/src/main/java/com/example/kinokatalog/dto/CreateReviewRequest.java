package com.example.kinokatalog.dto;

import lombok.Data;

@Data
public class CreateReviewRequest {
    private Integer rating;
    private String reviewText;
}
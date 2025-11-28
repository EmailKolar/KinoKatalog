package com.example.kinokatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Integer id;
    private String username; // from user
    private Integer movieId; // from movie
    private Integer rating;
    private String reviewText;
    private LocalDateTime createdAt;
}

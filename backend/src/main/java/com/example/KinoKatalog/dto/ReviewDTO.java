package com.example.KinoKatalog.dto;
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

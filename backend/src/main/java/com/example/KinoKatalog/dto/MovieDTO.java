package com.example.KinoKatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
    private Integer id;
    private Integer tmdbId;
    private String title;
    private String overview;
    private LocalDate releaseDate;
    private Integer runtime;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private String posterUrl;
    private LocalDateTime createdAt;
}

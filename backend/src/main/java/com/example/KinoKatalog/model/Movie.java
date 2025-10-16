package com.example.KinoKatalog.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    private Integer id;

    private String title;
    private String overview;
    private LocalDate releaseDate;
    private Integer runtime;
    private BigDecimal averageRating;


}

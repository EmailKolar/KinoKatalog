package com.example.kinokatalog.persistence.sql.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private Integer tmdbId;

    private String title;

    @Column(length = 5000)
    private String overview;

    private LocalDate releaseDate;
    private Integer runtime;

    @Column(precision = 4, scale = 2, name = "average_rating")
    private BigDecimal averageRating = BigDecimal.ZERO;

    private Integer reviewCount = 0;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @ManyToMany
    @JoinTable(
        name = "movie_genres",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<GenreEntity> genres;
    @ManyToMany
    @JoinTable(
            name = "movie_tags",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<TagEntity> tags;

    @OneToMany(mappedBy = "movieEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<MovieCrewEntity> crew;

    @OneToMany(mappedBy = "movieEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<MovieCastEntity> cast;

    @OneToMany(mappedBy = "movieEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<CompanyMovieEntity> companies;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

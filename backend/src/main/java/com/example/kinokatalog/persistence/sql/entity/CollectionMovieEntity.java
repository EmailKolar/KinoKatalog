package com.example.kinokatalog.persistence.sql.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Table(
        name = "collection_movies",
        uniqueConstraints = @UniqueConstraint(columnNames = {"collection_id", "movie_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionMovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "collection_id", nullable = false)
    private CollectionEntity collection;

    @ManyToOne(optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieEntity movie;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}


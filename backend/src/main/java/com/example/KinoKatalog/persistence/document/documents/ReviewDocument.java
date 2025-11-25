package com.example.KinoKatalog.persistence.document.documents;

import com.example.KinoKatalog.persistence.document.embedded.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("reviews")
@Builder
public class ReviewDocument {

    @Id
    private ObjectId id;

    private String movieId;
    private String userId;

    private Integer rating;
    private String reviewText;

    private LocalDateTime createdAt;

    private List<Comment> comments;
}

package com.example.KinoKatalog.persistence.graph.nodes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Data
@RequiredArgsConstructor
@Node("Review")
public class ReviewNode {

    @Id
    @GeneratedValue
    private String id;


    private Integer rating;
    private String reviewText;
    private LocalDateTime createdAt;

    @Relationship(type = "ON", direction = Relationship.Direction.INCOMING)
    private CommentNode comment;

    @Relationship(type = "FOR", direction = Relationship.Direction.OUTGOING)
    private MovieNode movie;

    @Relationship(type = "WROTE_REVIEW", direction = Relationship.Direction.INCOMING)
    private UserNode user;

}

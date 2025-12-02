package com.example.kinokatalog.persistence.graph.nodes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@Data
@RequiredArgsConstructor
@Node("Review")
public class ReviewNode {

    @Id
    @GeneratedValue
    private Long id;

    private Integer rating;
    private String reviewText;
    private LocalDateTime createdAt;

    private Integer sqlId;


    @Relationship(type = "ON", direction = Relationship.Direction.INCOMING)
    private List<CommentNode> comments;

    @Relationship(type = "FOR", direction = Relationship.Direction.OUTGOING)
    private MovieNode movie;

    @Relationship(type = "WROTE_REVIEW", direction = Relationship.Direction.INCOMING)
    private UserNode user;
}

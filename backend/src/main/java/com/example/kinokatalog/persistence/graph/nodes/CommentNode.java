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

@Builder
@AllArgsConstructor
@Data
@RequiredArgsConstructor
@Node("Comment")
public class CommentNode {

    @Id
    @GeneratedValue
    private String id;

    private String text;
    private LocalDateTime createdAt;

    @Relationship(type = "COMMENTED_ON" , direction = Relationship.Direction.INCOMING)
    private UserNode user;

    @Relationship(type = "ON" , direction = Relationship.Direction.OUTGOING)
    private ReviewNode review;

}

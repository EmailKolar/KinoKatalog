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
@Node("Collection")
public class CollectionNode {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String description;
    private LocalDateTime createdAt;

    @Relationship(type="CREATED_COLLECTION", direction = Relationship.Direction.INCOMING)
    private UserNode user;

    @Relationship(type = "CONTAINS_MOVIE", direction = Relationship.Direction.OUTGOING)
    private List<MovieNode> movies;


}

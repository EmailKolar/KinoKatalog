package com.example.kinokatalog.persistence.graph.relationships;

import com.example.kinokatalog.persistence.graph.nodes.MovieNode;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDateTime;

@RelationshipProperties
public class WatchlistRelation {

    @Id
    @GeneratedValue
    private String id;


    private LocalDateTime addedAt;
    private LocalDateTime updatedAt;

    @TargetNode
    private MovieNode movie;
}

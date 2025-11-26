package com.example.KinoKatalog.persistence.graph.relationships;

import com.example.KinoKatalog.persistence.graph.nodes.MovieNode;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDateTime;

@RelationshipProperties
public class WatchlistRelation {

    @Id
    @GeneratedValue
    private Integer id;

    private LocalDateTime addedAt;
    private LocalDateTime updatedAt;

    @TargetNode
    private MovieNode movie;
}

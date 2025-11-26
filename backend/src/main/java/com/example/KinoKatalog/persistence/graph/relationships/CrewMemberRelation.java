package com.example.KinoKatalog.persistence.graph.relationships;

import com.example.KinoKatalog.persistence.graph.nodes.MovieNode;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class CrewMemberRelation {

    @Id
    @GeneratedValue
    private Long id;
    private String job;

    @TargetNode
    private MovieNode movie;
}
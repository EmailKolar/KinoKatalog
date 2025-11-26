package com.example.KinoKatalog.persistence.graph.nodes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Builder
@AllArgsConstructor
@Data
@RequiredArgsConstructor
@Node("Genre")
public class GenreNode {

    @Id
    private Integer id;
    private String name;

    @Relationship(type = "HAS_GENRE", direction = Relationship.Direction.INCOMING)
    private MovieNode movie;
}

package com.example.KinoKatalog.persistence.graph.nodes;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Node("Tag")
public class TagNode {


    @Id
    @GeneratedValue
    private String id;

    private String name;

    @Relationship(type = "HAS_TAG", direction = Relationship.Direction.INCOMING)
    private MovieNode movie;


}

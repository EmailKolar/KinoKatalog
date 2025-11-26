package com.example.KinoKatalog.persistence.graph.nodes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Builder
@AllArgsConstructor
@Data
@RequiredArgsConstructor
@Node("Company")
public class CompanyNode {

    @Id
    @GeneratedValue
    private String id;


    private String name;
    private String originCountry;

    @Relationship(type = "PRODUCED_BY", direction = Relationship.Direction.INCOMING)
    private MovieNode movie;
}

package com.example.kinokatalog.persistence.graph.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Builder
@AllArgsConstructor
@Data
@RequiredArgsConstructor
@Node("Company")
public class CompanyNode {

    @Id
    @GeneratedValue
    private Long id;


    private String name;
    private String originCountry;
    private Integer sqlId;

    @JsonIgnore
    @Relationship(type = "PRODUCED_BY", direction = Relationship.Direction.INCOMING)
    private List<MovieNode> movies;
}

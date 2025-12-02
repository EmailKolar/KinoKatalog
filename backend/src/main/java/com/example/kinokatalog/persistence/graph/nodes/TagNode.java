package com.example.kinokatalog.persistence.graph.nodes;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Node("Tag")
public class TagNode {


    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Integer tmdbId;

    @Relationship(type = "HAS_TAG", direction = Relationship.Direction.INCOMING)
    private List<MovieNode> movies = new ArrayList<>();


}

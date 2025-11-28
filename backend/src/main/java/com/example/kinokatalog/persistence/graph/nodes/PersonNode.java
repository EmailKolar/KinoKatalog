package com.example.kinokatalog.persistence.graph.nodes;

import com.example.kinokatalog.persistence.graph.relationships.ActedInRelation;
import com.example.kinokatalog.persistence.graph.relationships.CrewMemberRelation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@Data
@RequiredArgsConstructor
@Node("Person")
public class PersonNode {

    @Id
    @GeneratedValue
    private String id;


    private Integer tmdbId;
    private String name;
    private String biography;
    private LocalDate birthDate;


    @Relationship(type = "ACTED_IN")
    private List<ActedInRelation> actedIn;

    @Relationship(type = "CREW_MEMBER")
    private List<CrewMemberRelation> crew;
}

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
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Builder
@AllArgsConstructor
@Data
@RequiredArgsConstructor
@Node("Person")
public class PersonNode {

    @Id
    @GeneratedValue
    private Long id;


    private Integer tmdbId;
    private String name;
    private String biography;
    private LocalDate birthDate;




    @Relationship(type = "ACTED_IN", direction = OUTGOING)
    private List<ActedInRelation> actedIn = new ArrayList<>();




    @Relationship(type = "CREW_MEMBER", direction = OUTGOING)
    private List<CrewMemberRelation> crewJobs = new ArrayList<>();

}

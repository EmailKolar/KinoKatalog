package com.example.kinokatalog.persistence.graph.nodes;


import com.example.kinokatalog.persistence.graph.relationships.ActedInRelation;
import com.example.kinokatalog.persistence.graph.relationships.CrewMemberRelation;
import com.example.kinokatalog.persistence.graph.relationships.WatchlistRelation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;

@Node("Movie")
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Data
public class MovieNode {

    @Id
    @GeneratedValue
    private Long id;


    private Integer tmdbId;

    private String title;

    private String overview;

    private LocalDate releaseDate;

    private String posterPath;

    private Integer runtime;

    private BigDecimal averageRating;

    private Integer reviewCount;

    private LocalDateTime createdAt;

    @Relationship(type = "HAS_GENRE", direction = Relationship.Direction.OUTGOING)
    private List<GenreNode> genres = new ArrayList<>();


    @Relationship(type = "HAS_TAG", direction = Relationship.Direction.OUTGOING)
    private List<TagNode> tags = new ArrayList<>();


    @Relationship(type = "PRODUCED_BY", direction = Relationship.Direction.OUTGOING)
    private List<CompanyNode> companies = new ArrayList<>();


    @Relationship(type = "CONTAINS_MOVIE", direction = INCOMING)
    private CollectionNode collection;

    @Relationship(type = "FOR", direction = INCOMING)
    private ReviewNode review;

    @Relationship(type = "ACTED_IN", direction = INCOMING)
    private List<PersonNode> cast; // NO RELATION CLASS HERE

    @Relationship(type = "CREW_MEMBER", direction = INCOMING)
    private List<PersonNode> crew; // NO RELATION CLASS HERE

    @Relationship(type = "HAS_IN_WATCHLIST", direction = INCOMING)
    private List<UserNode> watchlistedBy; // NO RELATION CLASS HERE


}

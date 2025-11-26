package com.example.KinoKatalog.persistence.graph.nodes;


import com.example.KinoKatalog.persistence.graph.relationships.ActedInRelation;
import com.example.KinoKatalog.persistence.graph.relationships.CrewMemberRelation;
import com.example.KinoKatalog.persistence.graph.relationships.WatchlistRelation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Node("Movie")
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Data
public class MovieNode {

    @Id
    private Integer id;

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
    private GenreNode genre;

    @Relationship(type = "HAS_TAG", direction = Relationship.Direction.OUTGOING)
    private TagNode tag;

    @Relationship(type = "PRODUCED_BY", direction = Relationship.Direction.OUTGOING)
    private CompanyNode company;

    @Relationship(type = "ACTED_IN", direction = Relationship.Direction.INCOMING)
    private List<ActedInRelation> cast;

    @Relationship(type = "CREW_MEMBER", direction = Relationship.Direction.INCOMING)
    private List<CrewMemberRelation> crew;

    @Relationship(type = "CONTAINS_MOVIE", direction = Relationship.Direction.INCOMING)
    private CollectionNode collection;

    @Relationship(type = "HAS_IN_WATCHLIST", direction = Relationship.Direction.INCOMING)
    private List<WatchlistRelation> watchlistedBy;

    @Relationship(type = "FOR", direction = Relationship.Direction.INCOMING)
    private ReviewNode review;









}

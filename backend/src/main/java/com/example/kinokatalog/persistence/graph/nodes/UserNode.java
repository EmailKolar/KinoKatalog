package com.example.kinokatalog.persistence.graph.nodes;

import com.example.kinokatalog.persistence.graph.relationships.WatchlistRelation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Builder
@AllArgsConstructor
@Data
@RequiredArgsConstructor
@Node("User")
public class UserNode {

    @Id
    @GeneratedValue
    private Long id;


    private String username;
    private String email;
    private String passwordHash;
    private Boolean isVerified;
    private String fullName;
    private LocalDateTime createdAt;
    private String role;

    @Relationship(type = "WROTE_REVIEW", direction = OUTGOING)
    private ReviewNode review;

    @Relationship(type = "WROTE_COMMENT", direction = OUTGOING)
    private CommentNode comment;

    @Relationship(type = "CREATED_COLLECTION", direction = OUTGOING)
    private CollectionNode collection;


    @Relationship(type = "HAS_IN_WATCHLIST", direction = OUTGOING)
    private List<WatchlistRelation> watchlist = new ArrayList<>();

}

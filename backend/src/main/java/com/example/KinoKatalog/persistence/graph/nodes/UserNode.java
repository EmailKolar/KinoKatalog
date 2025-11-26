package com.example.KinoKatalog.persistence.graph.nodes;

import com.example.KinoKatalog.persistence.graph.relationships.WatchlistRelation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@Data
@RequiredArgsConstructor
@Node("User")
public class UserNode {

    @Id
    private Integer id;

    private String username;
    private String email;
    private String passwordHash;
    private Boolean isVerified;
    private String fullName;
    private LocalDateTime createdAt;
    private String role;

    @Relationship(type = "WROTE_REVIEW", direction = Relationship.Direction.OUTGOING)
    private ReviewNode review;

    @Relationship(type = "WROTE_COMMENT", direction = Relationship.Direction.OUTGOING)
    private CommentNode comment;

    @Relationship(type = "CREATED_COLLECTION", direction = Relationship.Direction.OUTGOING)
    private CollectionNode collection;

    @Relationship(type = "HAS_IN_WATCHLIST", direction = Relationship.Direction.OUTGOING)
    private List<WatchlistRelation> watchlist;
}

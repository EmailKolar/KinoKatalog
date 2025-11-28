package com.example.kinokatalog.persistence.graph.repository;

import com.example.kinokatalog.persistence.graph.nodes.ReviewNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ReviewNodeRepository extends Neo4jRepository<ReviewNode, Long> {
}

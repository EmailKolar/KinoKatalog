package com.example.kinokatalog.persistence.graph.repository;

import com.example.kinokatalog.persistence.graph.nodes.MovieNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface MovieNodeRepository extends Neo4jRepository<MovieNode, Long> {
}

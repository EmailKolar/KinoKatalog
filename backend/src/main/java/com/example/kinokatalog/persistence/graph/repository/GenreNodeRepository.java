package com.example.kinokatalog.persistence.graph.repository;


import com.example.kinokatalog.persistence.graph.nodes.GenreNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface GenreNodeRepository extends Neo4jRepository<GenreNode, String> {
}

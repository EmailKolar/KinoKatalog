package com.example.kinokatalog.persistence.graph.repository;

import com.example.kinokatalog.persistence.graph.nodes.CollectionNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CollectionNodeRepository extends Neo4jRepository<CollectionNode, String> {
}

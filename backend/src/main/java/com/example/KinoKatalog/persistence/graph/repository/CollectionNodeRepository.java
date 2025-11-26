package com.example.KinoKatalog.persistence.graph.repository;

import com.example.KinoKatalog.persistence.graph.nodes.CollectionNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CollectionNodeRepository extends Neo4jRepository<CollectionNode, String> {
}

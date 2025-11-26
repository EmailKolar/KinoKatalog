package com.example.KinoKatalog.persistence.graph.repository;

import com.example.KinoKatalog.persistence.graph.nodes.CommentNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CommentNodeRepository extends Neo4jRepository<CommentNode, String> {
}

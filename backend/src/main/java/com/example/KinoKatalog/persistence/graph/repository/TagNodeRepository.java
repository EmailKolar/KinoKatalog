package com.example.KinoKatalog.persistence.graph.repository;

import com.example.KinoKatalog.persistence.graph.nodes.TagNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface TagNodeRepository extends Neo4jRepository<TagNode, String> {
}

package com.example.KinoKatalog.persistence.graph.repository;

import com.example.KinoKatalog.persistence.graph.nodes.PersonNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PersonNodeRepository extends Neo4jRepository<PersonNode, String> {
}

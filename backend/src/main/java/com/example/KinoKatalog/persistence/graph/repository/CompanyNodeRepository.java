package com.example.KinoKatalog.persistence.graph.repository;

import com.example.KinoKatalog.persistence.graph.nodes.CompanyNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CompanyNodeRepository extends Neo4jRepository<CompanyNode, Integer> {
}

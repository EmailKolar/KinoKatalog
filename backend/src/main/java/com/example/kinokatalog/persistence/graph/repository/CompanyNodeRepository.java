package com.example.kinokatalog.persistence.graph.repository;

import com.example.kinokatalog.persistence.graph.nodes.CompanyNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CompanyNodeRepository extends Neo4jRepository<CompanyNode, Long> {
}

package com.example.kinokatalog.persistence.graph.repository;

import com.example.kinokatalog.persistence.graph.nodes.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UserNodeRepository extends Neo4jRepository<UserNode, Long> {
}

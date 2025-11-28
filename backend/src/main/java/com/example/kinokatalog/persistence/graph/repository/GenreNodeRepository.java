package com.example.kinokatalog.persistence.graph.repository;


import com.example.kinokatalog.persistence.graph.nodes.GenreNode;
import com.example.kinokatalog.persistence.graph.nodes.MovieNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface GenreNodeRepository extends Neo4jRepository<GenreNode, Long> {

    GenreNode findByTmdbId(Integer tmdbId);

}

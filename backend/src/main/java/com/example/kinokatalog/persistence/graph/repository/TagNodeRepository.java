package com.example.kinokatalog.persistence.graph.repository;

import com.example.kinokatalog.persistence.graph.nodes.TagNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface TagNodeRepository extends Neo4jRepository<TagNode, Long> {

    TagNode findByTmdbId(Integer tmdbId);


}

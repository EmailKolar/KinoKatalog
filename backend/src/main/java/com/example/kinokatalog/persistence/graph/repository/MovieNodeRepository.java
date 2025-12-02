package com.example.kinokatalog.persistence.graph.repository;

import com.example.kinokatalog.persistence.graph.nodes.MovieNode;
import com.example.kinokatalog.persistence.sql.entity.MovieGenreEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MovieNodeRepository extends Neo4jRepository<MovieNode, Long> {


    MovieNode findByTmdbId(Integer tmdbId);

    @Query("MATCH (m:Movie {tmdbId: $tmdbId}) RETURN m")
    MovieNode findNodeOnlyByTmdbId(@Param("tmdbId") Integer tmdbId);

}

package com.example.kinokatalog.migrator;


import com.example.kinokatalog.persistence.graph.nodes.*;
import com.example.kinokatalog.persistence.graph.repository.*;
import com.example.kinokatalog.persistence.sql.entity.*;

import com.example.kinokatalog.persistence.sql.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MigrationSqlToNeoService {

    // SQL repositories
    private final MovieSqlRepository movieSqlRepo;
    private final GenreSqlRepository genreSqlRepo;
    private final TagSqlRepository tagSqlRepo;
    private final CompanySqlRepository companySqlRepo;
    private final MovieGenreSqlRepository movieGenreSqlRepo;
    private final MovieTagSqlRepository movieTagSqlRepo;
    private final CompanyMovieSqlRepository companyMovieSqlRepo;
    private final MovieCastSqlRepository movieCastSqlRepo;
    private final MovieCrewSqlRepository movieCrewSqlRepo;
    private final PersonSqlRepository personSqlRepo;
    private final ReviewSqlRepository reviewSqlRepo;
    private final CommentSqlRepository commentSqlRepo;


    // Neo4j repositories
    private final MovieNodeRepository movieNodeRepo;
    private final GenreNodeRepository genreNodeRepo;
    private final TagNodeRepository tagNodeRepo;
    private final CompanyNodeRepository companyNodeRepo;
    private final PersonNodeRepository personNodeRepo;
    private final ReviewNodeRepository reviewNodeRepo;
    private final CommentNodeRepository commentNodeRepo;

    private final Neo4jClient neo4jClient;


    @Autowired
    public MigrationSqlToNeoService(
            MovieSqlRepository movieSqlRepo,
            GenreSqlRepository genreSqlRepo,
            TagSqlRepository tagSqlRepo,
            CompanySqlRepository companySqlRepo,
            MovieGenreSqlRepository movieGenreSqlRepo,
            MovieTagSqlRepository movieTagSqlRepo,
            CompanyMovieSqlRepository companyMovieSqlRepo, MovieCastSqlRepository movieCastSqlRepo, MovieCrewSqlRepository movieCrewSqlRepo, PersonSqlRepository personSqlRepo, ReviewSqlRepository reviewSqlRepo, CommentSqlRepository commentSqlRepo,
            MovieNodeRepository movieNodeRepo,
            GenreNodeRepository genreNodeRepo,
            TagNodeRepository tagNodeRepo,
            CompanyNodeRepository companyNodeRepo, PersonNodeRepository personNodeRepo, ReviewNodeRepository reviewNodeRepo, CommentNodeRepository commentNodeRepo,
            Neo4jClient neo4jClient
    ) {
        this.movieSqlRepo = movieSqlRepo;
        this.genreSqlRepo = genreSqlRepo;
        this.tagSqlRepo = tagSqlRepo;
        this.companySqlRepo = companySqlRepo;
        this.movieGenreSqlRepo = movieGenreSqlRepo;
        this.movieTagSqlRepo = movieTagSqlRepo;
        this.companyMovieSqlRepo = companyMovieSqlRepo;
        this.movieCastSqlRepo = movieCastSqlRepo;
        this.movieCrewSqlRepo = movieCrewSqlRepo;
        this.personSqlRepo = personSqlRepo;
        this.reviewSqlRepo = reviewSqlRepo;
        this.commentSqlRepo = commentSqlRepo;
        this.movieNodeRepo = movieNodeRepo;
        this.genreNodeRepo = genreNodeRepo;
        this.tagNodeRepo = tagNodeRepo;
        this.companyNodeRepo = companyNodeRepo;
        this.personNodeRepo = personNodeRepo;
        this.reviewNodeRepo = reviewNodeRepo;
        this.commentNodeRepo = commentNodeRepo;
        this.neo4jClient = neo4jClient;
    }


    // ---------------------------------------------------------------------
    // MAIN ENTRY POINT
    // ---------------------------------------------------------------------
    public void migrate() {

        migrateMovies();
        migrateGenres();
        migrateTags();
        migrateCompanies();
        migratePersons();

        migrateMovieGenres();
        migrateMovieTags();
        migrateMovieCompanies();
        migrateMovieCast();
        migrateMovieCrew();


        System.out.println("Migration completed");
    }



    // ---------------------------------------------------------------------
    // NODE MIGRATIONS
    // ---------------------------------------------------------------------

    @Transactional("neo4jTransactionManager")
    public void migrateMovies() {
        List<MovieNode> movieNodes = movieSqlRepo.findAll().stream()
                .map(m -> MovieNode.builder()
                        .title(m.getTitle())
                        .overview(m.getOverview())
                        .releaseDate(m.getReleaseDate())
                        .runtime(m.getRuntime())
                        .posterPath(m.getPosterUrl())
                        .createdAt(m.getCreatedAt())
                        .averageRating(m.getAverageRating())
                        .reviewCount(m.getReviewCount())
                        .tmdbId(m.getTmdbId())
                        .genres(new ArrayList<>())
                        .tags(new ArrayList<>())
                        .companies(new ArrayList<>())
                        .build())
                .toList();

        movieNodeRepo.saveAll(movieNodes);
    }


    @Transactional("neo4jTransactionManager")
    public void migrateGenres() {
        genreSqlRepo.findAll().forEach(g ->
                genreNodeRepo.save(GenreNode.builder()
                        .name(g.getName())
                        .tmdbId(g.getId())
                        .build())
        );
    }


    @Transactional("neo4jTransactionManager")
    public void migrateTags() {
        tagSqlRepo.findAll().forEach(t ->
                tagNodeRepo.save(TagNode.builder()
                        .name(t.getName())
                        .tmdbId(t.getId())
                        .build())
        );
    }


    @Transactional("neo4jTransactionManager")
    public void migrateCompanies() {
        companySqlRepo.findAll().forEach(c ->
                companyNodeRepo.save(CompanyNode.builder()
                        .sqlId(c.getId())
                        .name(c.getName())
                        .originCountry(c.getOriginCountry())
                        .build())
        );
    }


    @Transactional("neo4jTransactionManager")
    public void migratePersons() {
        personSqlRepo.findAll().forEach(p ->
                personNodeRepo.save(PersonNode.builder()
                        .tmdbId(p.getTmdbId())
                        .name(p.getName())
                        .biography(p.getBio())
                        .birthDate(p.getBirthDate())
                        .build())
        );
    }

    @Transactional("neo4jTransactionManager")
    public void migrateReviewNodes() {
        reviewSqlRepo.findAll().forEach(r ->
                reviewNodeRepo.save(
                        ReviewNode.builder()
                                .rating(r.getRating())
                                .sqlId(r.getId())
                                .reviewText(r.getReviewText())
                                .createdAt(r.getCreatedAt())
                                .build()
                )
        );
    }

    @Transactional("neo4jTransactionManager")
    public void migrateCommentNodes() {
        commentSqlRepo.findAll().forEach(c ->
                commentNodeRepo.save(
                        CommentNode.builder()
                                .text(c.getCommentText())
                                .sqlId(c.getId())
                                .createdAt(c.getCreatedAt())
                                .build()
                )
        );
    }


    // ---------------------------------------------------------------------
    // RELATIONSHIP MIGRATIONS — USING NEO4J-CLIENT BULK UNWIND
    // ---------------------------------------------------------------------

    @Transactional("neo4jTransactionManager")
    public void migrateMovieGenres() {

        List<Map<String, Object>> rows = movieGenreSqlRepo.findAll().stream()
                .map(rel -> Map.<String, Object>of(
                        "mid", rel.getMovie().getTmdbId(),
                        "gid", rel.getGenre().getId()))
                .toList();

        neo4jClient.query("""
                UNWIND $rows AS r
                MATCH (m:Movie {tmdbId: r.mid})
                MATCH (g:Genre {tmdbId: r.gid})
                MERGE (m)-[:HAS_GENRE]->(g)
            """)
                .bind(rows).to("rows")
                .run();
    }


    @Transactional("neo4jTransactionManager")
    public void migrateMovieTags() {

        List<Map<String, Object>> rows = movieTagSqlRepo.findAll().stream()
                .map(rel -> Map.<String, Object>of(
                        "mid", rel.getMovie().getTmdbId(),
                        "tid", rel.getTag().getId()))
                .toList();

        neo4jClient.query("""
                UNWIND $rows AS r
                MATCH (m:Movie {tmdbId: r.mid})
                MATCH (t:Tag {tmdbId: r.tid})
                MERGE (m)-[:HAS_TAG]->(t)
            """)
                .bind(rows).to("rows")
                .run();
    }


    @Transactional("neo4jTransactionManager")
    public void migrateMovieCompanies() {

        List<Map<String, Object>> rows = companyMovieSqlRepo.findAll().stream()
                .map(rel -> Map.<String, Object>of(
                        "mid", rel.getMovieEntity().getTmdbId(),
                        "cid", rel.getCompanyEntity().getId()))
                .toList();

        neo4jClient.query("""
                UNWIND $rows AS r
                MATCH (m:Movie {tmdbId: r.mid})
                MATCH (c:Company {sqlId: r.cid})
                MERGE (m)-[:PRODUCED_BY]->(c)
            """)
                .bind(rows).to("rows")
                .run();
    }

    @Transactional("neo4jTransactionManager")
    public void migrateMovieCast() {

        List<Map<String, Object>> rows = movieCastSqlRepo.findAll().stream()
                .map(rel -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("mid", rel.getMovieEntity().getTmdbId());
                    m.put("pid", rel.getPersonEntity().getTmdbId());
                    m.put("character", rel.getCharacter());
                    m.put("billing", rel.getBillingOrder());
                    return m;
                })
                .toList();

        neo4jClient.query("""
        UNWIND $rows AS r
        MATCH (m:Movie {tmdbId: r.mid})
        MATCH (p:Person {tmdbId: r.pid})
        MERGE (p)-[rel:ACTED_IN]->(m)
        SET rel.character = r.character,
            rel.billingOrder = r.billing
    """)
                .bind(rows).to("rows")
                .run();
    }
    @Transactional("neo4jTransactionManager")
    public void migrateMovieCrew() {

        List<Map<String, Object>> rows = movieCrewSqlRepo.findAll().stream()
                .map(rel -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("mid", rel.getMovieEntity().getTmdbId());
                    m.put("pid", rel.getPersonEntity().getTmdbId());
                    m.put("job", rel.getJob());
                    return m;
                })
                .toList();

        neo4jClient.query("""
        UNWIND $rows AS r
        MATCH (m:Movie {tmdbId: r.mid})
        MATCH (p:Person {tmdbId: r.pid})
        MERGE (p)-[rel:CREW_MEMBER]->(m)
        SET rel.job = r.job
    """)
                .bind(rows).to("rows")
                .run();
    }

    @Transactional("neo4jTransactionManager")
    public void migrateReviewRelations() {

        List<Map<String, Object>> rows = reviewSqlRepo.findAll().stream()
                .map(r -> Map.<String,Object>of(
                        "rid", r.getId(),                       // Review sqlId
                        "uid", r.getUserEntity().getId(),       // User sqlId
                        "mid", r.getMovieEntity().getTmdbId()   // Movie tmdbId
                ))
                .toList();

        neo4jClient.query("""
        UNWIND $rows AS r
        MATCH (rev:Review {sqlId: r.rid})
        MATCH (u:User {sqlId: r.uid})
        MATCH (m:Movie {tmdbId: r.mid})
        MERGE (u)-[:WROTE_REVIEW]->(rev)
        MERGE (rev)-[:FOR]->(m)
    """)
                .bind(rows).to("rows")
                .run();
    }

    @Transactional("neo4jTransactionManager")
    public void migrateCommentRelations() {

        List<Map<String, Object>> rows = commentSqlRepo.findAll().stream()
                .map(c -> Map.<String,Object>of(
                        "cid", c.getId(),                      // Comment sqlId
                        "uid", c.getUserEntity().getId(),      // User sqlId
                        "rid", c.getReviewEntity().getId()     // Review sqlId
                ))
                .toList();

        neo4jClient.query("""
        UNWIND $rows AS r
        MATCH (c:Comment {sqlId: r.cid})
        MATCH (u:User {sqlId: r.uid})
        MATCH (rev:Review {sqlId: r.rid})
        MERGE (u)-[:WROTE_COMMENT]->(c)
        MERGE (c)-[:ON]->(rev)
    """)
                .bind(rows).to("rows")
                .run();
    }









}

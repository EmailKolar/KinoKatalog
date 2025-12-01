package com.example.kinokatalog.migrator;


import com.example.kinokatalog.persistence.graph.nodes.CompanyNode;
import com.example.kinokatalog.persistence.graph.nodes.GenreNode;
import com.example.kinokatalog.persistence.graph.nodes.MovieNode;
import com.example.kinokatalog.persistence.graph.nodes.TagNode;
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
    public final MovieSqlRepository movieSqlRepo;
    public final PersonSqlRepository personSqlRepo;
    public final CompanySqlRepository companySqlRepo;
    public final ReviewSqlRepository reviewSqlRepo;
    public final UserSqlRepository userSqlRepo;
    public final CollectionSqlRepository collectionSqlRepo;
    public final CollectionMovieSqlRepository collectionMovieSqlRepo;
    public final WatchlistSqlRepository watchlistSqlRepo;
    public final GenreSqlRepository genreSqlRepo;
    public final TagSqlRepository tagSqlRepo;
    public final MovieNodeRepository movieNodeRepo;
    public final PersonNodeRepository personNodeRepo;
    public final CompanyNodeRepository companyNodeRepo;
    public final ReviewNodeRepository reviewNodeRepo;
    public final CommentNodeRepository commentNodeRepo;
    public final UserNodeRepository userNodeRepo;
    public final CollectionNodeRepository collectionNodeRepo;
    public final GenreNodeRepository genreNodeRepo;
    public final TagNodeRepository tagNodeRepo;
    public final DemoNodeRepository demoNodeRepo;
    public final MovieGenreSqlRepository movieGenreSqlRepo;
    public final CompanyMovieSqlRepository companyMovieSqlRepository;
    public final MovieTagSqlRepository movieTagSqlRepo;


    @Autowired
    private Neo4jClient neo4jClient;


    @Autowired
    public MigrationSqlToNeoService(GenreNodeRepository genreNodeRepo, DemoNodeRepository demoNodeRepo, MovieSqlRepository movieSqlRepo, PersonSqlRepository personSqlRepo, CompanySqlRepository companySqlRepo, ReviewSqlRepository reviewSqlRepo, UserSqlRepository userSqlRepo, CollectionSqlRepository collectionSqlRepo, CollectionMovieSqlRepository collectionMovieSqlRepo, WatchlistSqlRepository watchlistSqlRepo, GenreSqlRepository genreSqlRepo, TagSqlRepository tagSqlRepo, MovieNodeRepository movieNodeRepo, PersonNodeRepository personNodeRepo, CompanyNodeRepository companyNodeRepo, ReviewNodeRepository reviewNodeRepo, CommentNodeRepository commentNodeRepo, UserNodeRepository userNodeRepo, CollectionNodeRepository collectionNodeRepo, TagNodeRepository tagNodeRepo, MovieGenreSqlRepository movieGenreSqlRepo, CompanyMovieSqlRepository companyMovieSqlRepository, MovieTagSqlRepository movieTagSqlRepo) {
        this.movieSqlRepo = movieSqlRepo;
        this.personSqlRepo = personSqlRepo;
        this.companySqlRepo = companySqlRepo;
        this.reviewSqlRepo = reviewSqlRepo;
        this.userSqlRepo = userSqlRepo;
        this.collectionSqlRepo = collectionSqlRepo;
        this.collectionMovieSqlRepo = collectionMovieSqlRepo;
        this.watchlistSqlRepo = watchlistSqlRepo;
        this.genreSqlRepo = genreSqlRepo;
        this.tagSqlRepo = tagSqlRepo;
        this.movieNodeRepo = movieNodeRepo;
        this.personNodeRepo = personNodeRepo;
        this.companyNodeRepo = companyNodeRepo;
        this.reviewNodeRepo = reviewNodeRepo;
        this.commentNodeRepo = commentNodeRepo;
        this.userNodeRepo = userNodeRepo;
        this.collectionNodeRepo = collectionNodeRepo;
        this.genreNodeRepo = genreNodeRepo;
        this.tagNodeRepo = tagNodeRepo;
        this.demoNodeRepo = demoNodeRepo;
        this.movieGenreSqlRepo = movieGenreSqlRepo;
        this.companyMovieSqlRepository = companyMovieSqlRepository;
        this.movieTagSqlRepo = movieTagSqlRepo;
    }

    public void migrate() {

        List<MovieNode> movieNodeList = getMovies();
        saveMoviesToNeo(movieNodeList);

        System.out.println("PRINT1");

        List<GenreEntity> genreEntities = getGenreEntities();
        migrateGenres(genreEntities);

        System.out.println("PRINT2");


        List<TagEntity> tagEntities = getTagEntities();
        migrateTags(tagEntities);

        System.out.println("PRINT3");


        //migrateMovieGenres();

        migrateCompanies(getCompanyEntities());

        System.out.println("PRINT4");


       // migrateMoviesCompanies();

        migrateMovieRelations();

        System.out.println("PRINT5");


        /*migratePersons();
        migrateCompanies();
        migrateGenres();
        migrateTags();
        migrateCast();
        migrateCrew();
        migrateCompaniesOnMovies();
        migrateUsers();
        migrateReviews();
        migrateComments();
        migrateCollections();
        migrateWatchlists();*/
    }


    @Transactional
    private List<MovieNode> getMovies(){
        List<MovieNode> movieNodes = new ArrayList<>();
        movieSqlRepo.findAll().forEach(m -> {
            MovieNode movie = new MovieNode();
            //movie.setId(m.getId().toString());
            movie.setTitle(m.getTitle());
            movie.setReleaseDate(m.getReleaseDate());
            movie.setRuntime(m.getRuntime());
            movie.setOverview(m.getOverview());
            movie.setTmdbId(m.getTmdbId());
            movie.setAverageRating(m.getAverageRating());
            movie.setReviewCount(m.getReviewCount());
            movie.setPosterPath(m.getPosterUrl());
            movie.setCreatedAt(m.getCreatedAt());
            movie.setTags(new ArrayList<>());
            movie.setGenres(new ArrayList<>());
            movie.setCompanies(new ArrayList<>());
            movieNodes.add(movie);
        });
        return movieNodes;
    }

    @Transactional("neo4jTransactionManager")
    private void saveMoviesToNeo(List<MovieNode> movies){
        movieNodeRepo.saveAll(movies);
    }

    @Transactional
    private List<GenreEntity> getGenreEntities(){
        return genreSqlRepo.findAll();
    }

    @Transactional("neo4jTransactionManager")
    private void migrateGenres(List<GenreEntity> genreEntities){
        for (var g : genreEntities){
            GenreNode node = GenreNode.builder()
                    .name(g.getName())
                    .tmdbId(g.getId())
                    .build();
            genreNodeRepo.save(node);
        }
    }

    @Transactional
    private List<TagEntity> getTagEntities(){
        return tagSqlRepo.findAll();
    }

    @Transactional("neo4jTransactionManager")
    private void migrateTags(List<TagEntity> tagEntities){
        for (var t : tagEntities){
            TagNode node = TagNode.builder()
                    .name(t.getName())
                    .tmdbId(t.getId())
                    .build();
            tagNodeRepo.save(node);
        }
    }




    @Transactional
    private List<MovieGenreEntity> getMovieGenres(){
        return movieGenreSqlRepo.findAll();
    }
    @Transactional
    private List<MovieTagEntity> getMovieTags(){
        return movieTagSqlRepo.findAll();
    }



    @Transactional("neo4jTransactionManager")
    private MovieNode getMovieNodeByTmdbId(Integer tmdbId) {
        return movieNodeRepo.findNodeOnlyByTmdbId(tmdbId);

    }
    @Transactional("neo4jTransactionManager")
    private GenreNode getGenreNodeByTmdbId(MovieGenreEntity movieGenreEntity) {
        return genreNodeRepo.findByTmdbId(movieGenreEntity.getGenre().getId());
    }

    @Transactional("neo4jTransactionManager")
    public void migrateMovieGenresRepo() {
        getMovieGenres().forEach(rel -> {
            MovieNode movie = getMovieNodeByTmdbId(rel.getMovie().getTmdbId());
            GenreNode genre = getGenreNodeByTmdbId(rel);
            movie.getGenres().add(genre);
            genre.getMovies().add(movie);
            movieNodeRepo.save(movie);
            genreNodeRepo.save(genre);
        });
    }

    @Transactional("neo4jTransactionManager")
    public void migrateMovieTagsRepo() {
        getMovieTags().forEach(rel->{
            MovieNode movie = getMovieNodeByTmdbId(rel.getMovie().getTmdbId());
            TagNode tag = tagNodeRepo.findByTmdbId(rel.getTag().getId());

            movie.getTags().add(tag);
            tag.getMovies().add(movie);

            tagNodeRepo.save(tag);
            movieNodeRepo.save(movie);
        });
    }

    @Transactional("neo4jTransactionManager")
    public void migrateMovieRelations(){
        migrateMovieGenres();
        migrateMovieTags();
        migrateMovieCompanies();
    }


    @Transactional
    public List<CompanyEntity> getCompanyEntities(){
        return companySqlRepo.findAll();
    }


    @Transactional("neo4jTransactionManager")
    public void migrateCompanies(List<CompanyEntity> companyEntities){
        for (var c: companyEntities){
            CompanyNode cn = CompanyNode.builder()
                    .name(c.getName())
                    .originCountry(c.getOriginCountry())
                    .sqlId(c.getId())
                    //.movies(null)
                    .build();
            companyNodeRepo.save(cn);
        }
    }

    @Transactional("neo4jTransactionManager")
    public void migrateMoviesCompaniesRepo(){
        List<CompanyMovieEntity> companyMovieEntities = companyMovieSqlRepository.findAll();

        for(var cme: companyMovieEntities){
            MovieNode movie = getMovieNodeByTmdbId(cme.getMovieEntity().getTmdbId());
            movie.getCompanies().add(companyNodeRepo.findBySqlId(cme.getCompanyEntity().getId()));

            movieNodeRepo.save(movie);

            CompanyNode companyNode = companyNodeRepo.findBySqlId(cme.getCompanyEntity().getId());
            companyNode.getMovies().add(movie);

            companyNodeRepo.save(companyNode);
        }
    }


    @Transactional("neo4jTransactionManager")
    public void migrateMovieGenres() {

        List<Map<String, Object>> rels = movieGenreSqlRepo.findAll().stream()
                .map(rel -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("mid", rel.getMovie().getTmdbId());
                    m.put("gid", rel.getGenre().getId());
                    return m;
                })
                .toList();

        neo4jClient.query("""
        UNWIND $rels AS r
        MATCH (m:Movie {tmdbId: r.mid})
        MATCH (g:Genre {tmdbId: r.gid})
        MERGE (m)-[:HAS_GENRE]->(g)
    """)
                .bind(rels).to("rels")
                .run();
    }


    @Transactional("neo4jTransactionManager")
    public void migrateMovieTags() {

        List<Map<String, Object>> rels = movieTagSqlRepo.findAll().stream()
                .map(rel -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("mid", rel.getMovie().getTmdbId());
                    m.put("tid", rel.getTag().getId());
                    return m;
                })
                .toList();

        neo4jClient.query("""
        UNWIND $rels AS r
        MATCH (m:Movie {tmdbId: r.mid})
        MATCH (t:Tag {tmdbId: r.tid})
        MERGE (m)-[:HAS_TAG]->(t)
    """)
                .bind(rels).to("rels")
                .run();
    }
    @Transactional("neo4jTransactionManager")
    public void migrateMovieCompanies() {

        List<Map<String, Object>> rels = companyMovieSqlRepository.findAll().stream()
                .map(rel -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("mid", rel.getMovieEntity().getTmdbId());
                    m.put("cid", rel.getCompanyEntity().getId());
                    return m;
                })
                .toList();

        neo4jClient.query("""
        UNWIND $rels AS r
        MATCH (m:Movie {tmdbId: r.mid})
        MATCH (c:Company {sqlId: r.cid})
        MERGE (m)-[:PRODUCED_BY]->(c)
    """)
                .bind(rels).to("rels")
                .run();
    }




}

package com.example.kinokatalog.migrator;


import com.example.kinokatalog.persistence.graph.nodes.DemoNode;
import com.example.kinokatalog.persistence.graph.nodes.MovieNode;
import com.example.kinokatalog.persistence.graph.repository.*;
import com.example.kinokatalog.persistence.sql.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


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

    @Autowired
    public MigrationSqlToNeoService(GenreNodeRepository genreNodeRepo, DemoNodeRepository demoNodeRepo, MovieSqlRepository movieSqlRepo, PersonSqlRepository personSqlRepo, CompanySqlRepository companySqlRepo, ReviewSqlRepository reviewSqlRepo, UserSqlRepository userSqlRepo, CollectionSqlRepository collectionSqlRepo, CollectionMovieSqlRepository collectionMovieSqlRepo, WatchlistSqlRepository watchlistSqlRepo, GenreSqlRepository genreSqlRepo, TagSqlRepository tagSqlRepo, MovieNodeRepository movieNodeRepo, PersonNodeRepository personNodeRepo, CompanyNodeRepository companyNodeRepo, ReviewNodeRepository reviewNodeRepo, CommentNodeRepository commentNodeRepo, UserNodeRepository userNodeRepo, CollectionNodeRepository collectionNodeRepo, TagNodeRepository tagNodeRepo) {
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
    }

    public void migrate() {
        System.out.println("PRINT: IN MIGRATE");


        makeDemoMigration();
        //makeDemoMigration();
        //migrateMovies();
        List<MovieNode> movieNodeList = getMovies();
        saveMoviesToNeo(movieNodeList);
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

    @Transactional("neo4jTransactionManager")
    public void migrateMovies() {
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
            movieNodeRepo.save(movie);
        });
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
            movieNodes.add(movie);
        });
        System.out.println("PRINT: 1 movie: "+movieNodes.getFirst());
        System.out.println("PRINT: last movie: "+movieNodes.getLast());
        return movieNodes;
    }

    @Transactional("neo4jTransactionManager")
    private void saveMoviesToNeo(List<MovieNode> movies){
        System.out.println("Trying to SAVE !!!");
        movieNodeRepo.saveAll(movies);
    }


    @Transactional("neo4jTransactionManager")
    private void makeDemoMigration() {
        System.out.println("PRINT: IN MAKE DEMO");

        DemoNode demoNode = new DemoNode(null, "Demo Name");
        MovieNode movie = new MovieNode();
        movie.setTitle("working");
        demoNodeRepo.save(demoNode);
        movieNodeRepo.save(movie);

    }


}

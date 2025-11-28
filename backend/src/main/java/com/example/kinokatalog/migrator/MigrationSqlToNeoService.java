package com.example.kinokatalog.migrator;


import com.example.kinokatalog.persistence.graph.nodes.GenreNode;
import com.example.kinokatalog.persistence.graph.nodes.MovieNode;
import com.example.kinokatalog.persistence.graph.nodes.TagNode;
import com.example.kinokatalog.persistence.graph.repository.*;
import com.example.kinokatalog.persistence.sql.entity.GenreEntity;
import com.example.kinokatalog.persistence.sql.entity.MovieGenreEntity;

import com.example.kinokatalog.persistence.sql.entity.MovieGenreEntity;
import com.example.kinokatalog.persistence.sql.entity.TagEntity;
import com.example.kinokatalog.persistence.sql.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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

    @Autowired
    public MigrationSqlToNeoService(GenreNodeRepository genreNodeRepo, DemoNodeRepository demoNodeRepo, MovieSqlRepository movieSqlRepo, PersonSqlRepository personSqlRepo, CompanySqlRepository companySqlRepo, ReviewSqlRepository reviewSqlRepo, UserSqlRepository userSqlRepo, CollectionSqlRepository collectionSqlRepo, CollectionMovieSqlRepository collectionMovieSqlRepo, WatchlistSqlRepository watchlistSqlRepo, GenreSqlRepository genreSqlRepo, TagSqlRepository tagSqlRepo, MovieNodeRepository movieNodeRepo, PersonNodeRepository personNodeRepo, CompanyNodeRepository companyNodeRepo, ReviewNodeRepository reviewNodeRepo, CommentNodeRepository commentNodeRepo, UserNodeRepository userNodeRepo, CollectionNodeRepository collectionNodeRepo, TagNodeRepository tagNodeRepo, MovieGenreSqlRepository movieGenreSqlRepo) {
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
    }

    public void migrate() {

        List<MovieNode> movieNodeList = getMovies();
        saveMoviesToNeo(movieNodeList);

        List<GenreEntity> genreEntities = getGenreEntities();
        migrateGenres(genreEntities);

        List<TagEntity> tagEntities = getTagEntites();
        migrateTags(tagEntities);

        migrateMovieGenres();

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
            movie.setTags(null);
            movie.setGenres(null);
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
    private List<TagEntity> getTagEntites(){
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

    @Transactional("neo4jTransactionManager")
    private MovieNode getMovieNodeByTmdbId(MovieGenreEntity movieGenreEntity) {
        return movieNodeRepo.findNodeOnlyByTmdbId(movieGenreEntity.getMovie().getTmdbId());

    }
    @Transactional("neo4jTransactionManager")
    private GenreNode getGenreNodeByTmdbId(MovieGenreEntity movieGenreEntity) {
        return genreNodeRepo.findByTmdbId(movieGenreEntity.getGenre().getId());
    }

    @Transactional("neo4jTransactionManager")
    public void migrateMovieGenres() {
        getMovieGenres().forEach(rel -> {
            MovieNode movie = getMovieNodeByTmdbId(rel);
            GenreNode genre = getGenreNodeByTmdbId(rel);
            movie.getGenres().add(genre);
            movieNodeRepo.save(movie);
        });
    }



}

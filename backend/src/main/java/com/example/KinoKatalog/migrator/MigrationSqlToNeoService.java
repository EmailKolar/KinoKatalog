package com.example.KinoKatalog.migrator;


import com.example.KinoKatalog.persistence.graph.nodes.MovieNode;
import com.example.KinoKatalog.persistence.graph.repository.*;
import com.example.KinoKatalog.persistence.sql.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

    public MigrationSqlToNeoService(MovieSqlRepository movieSqlRepo, PersonSqlRepository personSqlRepo, CompanySqlRepository companySqlRepo, ReviewSqlRepository reviewSqlRepo, UserSqlRepository userSqlRepo, CollectionSqlRepository collectionSqlRepo, CollectionMovieSqlRepository collectionMovieSqlRepo, WatchlistSqlRepository watchlistSqlRepo, GenreSqlRepository genreSqlRepo, TagSqlRepository tagSqlRepo, MovieNodeRepository movieNodeRepo, PersonNodeRepository personNodeRepo, CompanyNodeRepository companyNodeRepo, ReviewNodeRepository reviewNodeRepo, CommentNodeRepository commentNodeRepo, UserNodeRepository userNodeRepo, CollectionNodeRepository collectionNodeRepo, GenreNodeRepository genreNodeRepo, TagNodeRepository tagNodeRepo) {
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
    }


    public void migrate() {
        migrateMovies();
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

    private void migrateMovies() {
        movieSqlRepo.findAll().forEach(m -> {
            MovieNode movie = new MovieNode();
            movie.setId(m.getId().toString());
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


}

package com.example.kinokatalog.migrator;

import com.example.kinokatalog.persistence.document.documents.*;
import com.example.kinokatalog.persistence.document.embedded.*;
import com.example.kinokatalog.persistence.document.repository.*;
import com.example.kinokatalog.persistence.sql.entity.*;
import com.example.kinokatalog.persistence.sql.repository.*;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MigrationService {

    private final MovieDocumentRepository movieDocRepo;
    private final MovieSqlRepository movieSqlRepo;
    private final PersonSqlRepository personSqlRepo;
    private final PersonDocumentRepository personDocRepo;
    private final CompanySqlRepository companySqlRepo;
    private final CompanyDocumentRepository companyDocRepo;
    private final ReviewSqlRepository reviewSqlRepo;
    private final ReviewDocumentRepository reviewDocRepo;
    private final UserSqlRepository userSqlRepo;
    private final UserDocumentRepository userDocRepo;
    private final CollectionSqlRepository collectionRepo;
    private final CollectionMovieSqlRepository collectionMovieRepo;
    private final WatchlistSqlRepository watchlistSqlRepo;


    public MigrationService(MovieDocumentRepository movieDocRepo, MovieSqlRepository movieSqlRepo, PersonSqlRepository personSqlRepo, PersonDocumentRepository personDocRepo, CompanySqlRepository companySqlRepo, CompanyDocumentRepository companyDocRepo, ReviewSqlRepository reviewSqlRepo, ReviewDocumentRepository reviewDocRepo, UserSqlRepository userSqlRepo, UserDocumentRepository userDocRepo, CollectionSqlRepository collectionRepo, CollectionMovieSqlRepository collectionMovieRepo, WatchlistSqlRepository watchlistSqlRepo) {
        this.movieDocRepo = movieDocRepo;
        this.movieSqlRepo = movieSqlRepo;
        this.personSqlRepo = personSqlRepo;
        this.personDocRepo = personDocRepo;
        this.companySqlRepo = companySqlRepo;
        this.companyDocRepo = companyDocRepo;
        this.reviewSqlRepo = reviewSqlRepo;
        this.reviewDocRepo = reviewDocRepo;
        this.userSqlRepo = userSqlRepo;
        this.userDocRepo = userDocRepo;
        this.collectionRepo = collectionRepo;
        this.collectionMovieRepo = collectionMovieRepo;
        this.watchlistSqlRepo = watchlistSqlRepo;
    }


    public void migrate() {
        List<MovieEntity> movies = getMovies();
        migrateMovies(movies);
        List<PersonEntity> people = getPersons();
        migratePersons(people);
        List<CompanyEntity> companies = getCompanies();
        migrateCompanies(companies);
        List<ReviewEntity> reviews = getReviews();
        migrateReviews(reviews);
        List<UserEntity> users = getUsers();
        migrateUsers(users);
    }

    @Transactional
    private List<MovieEntity> getMovies(){
        return movieSqlRepo.findAll();
    }

    @Transactional("MongoTransactionManager")
    private void migrateMovies(List<MovieEntity> movies) {
        for (var m : movies) {
            MovieDocument doc = MovieDocument.builder()
                    .title(m.getTitle())
                    .releaseDate(m.getReleaseDate())
                    .runtime(m.getRuntime())
                    .overview(m.getOverview())
                    .tmdbId(m.getTmdbId())
                    .averageRating(m.getAverageRating())
                    .reviewCount(m.getReviewCount())
                    .posterUrl(m.getPosterUrl())
                    .createdAt(m.getCreatedAt())
                    .genres(m.getGenres().stream().map(GenreEntity::getName).toList())
                    .tags(m.getTags().stream().map(TagEntity::getName).toList())
                    .crew(m.getCrew().stream().map(c -> CrewMember.builder()
                            .name(c.getPersonEntity().getName())
                            .tmdbId(c.getPersonEntity().getTmdbId())
                            .job(c.getJob())
                            .build()).toList()
                    )
                    .cast(m.getCast().stream().map(c -> CastMember.builder()
                            .name(c.getPersonEntity().getName())
                            .tmdbId(c.getPersonEntity().getTmdbId())
                            .character(c.getCharacter())
                            .billingOrder(c.getBillingOrder())
                            .build()).toList()
                    )
                    .companies(m.getCompanies().stream().map(c -> CompanyInfo.builder()
                            .name(c.getCompanyEntity().getName())
                            .originCountry(c.getCompanyEntity().getOriginCountry())
                            .build()).toList()
                    )
                    .build();
            movieDocRepo.save(doc);
            System.out.println("Migrated movie: " + m.getTitle());
        }
    }

    @Transactional
    private List<PersonEntity> getPersons() {
        return personSqlRepo.findAll();
    }

    @Transactional("MongoTransactionManager")
    private void migratePersons(List<PersonEntity> people) {

        for (var p : people) {
            PersonDocument doc = PersonDocument.builder()
                    .name(p.getName())
                    .tmdbId(p.getTmdbId())
                    .biography(p.getBio())
                    .birthDate(p.getBirthDate())
                    .build();

            personDocRepo.save(doc);
        }


    }

    @Transactional
    private List<CompanyEntity> getCompanies() {
        return companySqlRepo.findAll();
    }

    @Transactional("MongoTransactionManager")
    private void migrateCompanies(List<CompanyEntity> companies) {

        for(var c : companies) {
            CompanyDocument doc = CompanyDocument.builder()
                    .name(c.getName())
                    .originCountry(c.getOriginCountry())
                    .build();
            companyDocRepo.save(doc);
        }
    }

    @Transactional
    private List<ReviewEntity> getReviews() {
        return reviewSqlRepo.findAll();
    }

    @Transactional("MongoTransactionManager")
    private void migrateReviews(List<ReviewEntity> reviews) {


        for (ReviewEntity r : reviews) {

            // Convert SQL comments -> Mongo embeddable comments
            List<Comment> commentDocs = r.getComments().stream()
                    .map(c -> new Comment(
                            String.valueOf(c.getUserEntity().getId()),
                            c.getCommentText(),
                            c.getCreatedAt()
                    ))
                    .toList();

            ReviewDocument doc = ReviewDocument.builder()
                    .movieId(String.valueOf(r.getMovieEntity().getId()))
                    .userId(String.valueOf(r.getUserEntity().getId()))
                    .rating(r.getRating())
                    .reviewText(r.getReviewText())
                    .createdAt(r.getCreatedAt())
                    .comments(commentDocs)
                    .build();

            reviewDocRepo.save(doc);
        }
    }

    @Transactional
    private List<UserEntity> getUsers() {
        return userSqlRepo.findAll();
    }

    @Transactional
    private List<CollectionEntity> getCollectionsByUserId(Integer userId) {
        return collectionRepo.findByUserId(userId);
    }

    @Transactional
    private List<CollectionMovieEntity> getCollectionMoviesByCollectionId(Integer collectionId) {
        return collectionMovieRepo.findByCollectionId(collectionId);
    }

    @Transactional
    private List<WatchlistEntity> getWatchlistsByUserId(Integer userId) {
        return watchlistSqlRepo.findByUserId(userId);
    }

    @Transactional("MongoTransactionManager")
    private void migrateUsers(List<UserEntity> users) {

        for (UserEntity u : users) {

            //List<CollectionEntity> collectionEntities = collectionRepo.findByUserId(u.getId());
            List<CollectionEntity> collectionEntities = getCollectionsByUserId(u.getId());


            List<UserCollection> collectionDocs = collectionEntities.stream()
                    .map(c -> {
                        // movies inside this collection
                        //List<CollectionMovieEntity> movieEntities = collectionMovieRepo.findByCollectionId(c.getId());
                        List<CollectionMovieEntity> movieEntities = getCollectionMoviesByCollectionId(c.getId());


                        List<CollectionMovieEntity> movies = movieEntities.stream()
                                .map(cm -> CollectionMovieEntity.builder()
                                        .movieId(cm.getMovieId())
                                        .createdAt(cm.getCreatedAt())
                                        .build()
                                ).toList();
                        return UserCollection.builder()
                                .name(c.getName())
                                .description(c.getDescription())
                                .createdAt(c.getCreatedAt())
                                .movieIds(movies.stream()
                                        .map(CollectionMovieEntity::getMovieId)
                                        .toList())
                                .build();
                    }).toList();

            //List<WatchlistEntity> watchlistEntities = watchlistSqlRepo.findByUserId(u.getId());
            List<WatchlistEntity> watchlistEntities = getWatchlistsByUserId(u.getId());
            Watchlist watchlistDoc = Watchlist.builder()
                    .movieIds(
                            watchlistEntities.stream()
                                    .map(w -> w.getMovie().getId()) // SQL integer IDs
                                    .toList()
                    )
                    .updatedAt(
                            watchlistEntities.stream()
                                    .map(WatchlistEntity::getAddedAt)
                                    .max(LocalDateTime::compareTo)
                                    .orElse(null)
                    )
                    .build();


            UserDocument doc = UserDocument.builder()
                            .username(u.getUsername())
                            .email(u.getEmail())
                            .passwordHash(u.getPasswordHash())
                            .isVerified(u.getIsVerified())
                            .role(u.getRole())
                            .createdAt(u.getCreatedAt())
                            .collections(collectionDocs)
                            .watchlist(watchlistDoc)
                            .build();

            userDocRepo.save(doc);
        }

    }

}


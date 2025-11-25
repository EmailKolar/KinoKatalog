package com.example.KinoKatalog.migrator;

import com.example.KinoKatalog.persistence.document.documents.*;
import com.example.KinoKatalog.persistence.document.embedded.*;
import com.example.KinoKatalog.persistence.document.repository.*;
import com.example.KinoKatalog.persistence.sql.entity.*;
import com.example.KinoKatalog.persistence.sql.repository.*;
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


    public MigrationService(MovieDocumentRepository movieDocRepo,
                            MovieSqlRepository movieSqlRepo, PersonSqlRepository personSqlRepo,
                            PersonDocumentRepository personDocRepo,
                            CompanySqlRepository companySqlRepo,
                            CompanyDocumentRepository companyDocRepo,
                            ReviewSqlRepository reviewSqlRepo,
                            ReviewDocumentRepository reviewDocRepo,
                            UserSqlRepository userSqlRepo,
                            UserDocumentRepository userDocRepo,
                            CollectionSqlRepository collectionRepo,
                            CollectionMovieSqlRepository collectionMovieRepo,
                            WatchlistSqlRepository watchlistSqlRepo) {
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

    @Transactional
    public void migrate() {
        migrateMovies();
        migratePersons();
        migrateCompanies();
        migrateReviews();
        migrateUsers();
    }

    private void migrateMovies() {
        var movies = movieSqlRepo.findAll();

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


    private void migratePersons() {
        var people = personSqlRepo.findAll();

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
    private void migrateCompanies() {
        var companies = companySqlRepo.findAll();

        for(var c : companies) {
            CompanyDocument doc = CompanyDocument.builder()
                    .name(c.getName())
                    .originCountry(c.getOriginCountry())
                    .build();
            companyDocRepo.save(doc);
        }
    }
    private void migrateReviews() {
        List<ReviewEntity> reviews = reviewSqlRepo.findAll();

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
    private void migrateUsers() {

        List<UserEntity> users = userSqlRepo.findAll();

        for (UserEntity u : users) {

            List<CollectionEntity> collectionEntities = collectionRepo.findByUserId(u.getId());

            List<UserCollection> collectionDocs = collectionEntities.stream()
                    .map(c -> {
                        // movies inside this collection
                        List<CollectionMovieEntity> movieEntities =
                                collectionMovieRepo.findByCollectionId(c.getId());

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

            List<WatchlistEntity> watchlistEntities = watchlistSqlRepo.findByUserId(u.getId());
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


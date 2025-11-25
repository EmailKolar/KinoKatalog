package com.example.KinoKatalog.migrator;

import com.example.KinoKatalog.persistence.document.documents.CompanyDocument;
import com.example.KinoKatalog.persistence.document.documents.MovieDocument;
import com.example.KinoKatalog.persistence.document.documents.PersonDocument;
import com.example.KinoKatalog.persistence.document.documents.ReviewDocument;
import com.example.KinoKatalog.persistence.document.embedded.CastMember;
import com.example.KinoKatalog.persistence.document.embedded.Comment;
import com.example.KinoKatalog.persistence.document.embedded.CompanyInfo;
import com.example.KinoKatalog.persistence.document.embedded.CrewMember;
import com.example.KinoKatalog.persistence.document.repository.CompanyDocumentRepository;
import com.example.KinoKatalog.persistence.document.repository.MovieDocumentRepository;
import com.example.KinoKatalog.persistence.document.repository.PersonDocumentRepository;
import com.example.KinoKatalog.persistence.document.repository.ReviewDocumentRepository;
import com.example.KinoKatalog.persistence.sql.entity.*;
import com.example.KinoKatalog.persistence.sql.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    public MigrationService(MovieDocumentRepository movieDocRepo,
                            MovieSqlRepository movieSqlRepo, PersonSqlRepository personSqlRepo,
                            PersonDocumentRepository personDocRepo,
                            CompanySqlRepository companySqlRepo,
                            CompanyDocumentRepository companyDocRepo,
                            ReviewSqlRepository reviewSqlRepo,
                            ReviewDocumentRepository reviewDocRepo) {
        this.movieDocRepo = movieDocRepo;
        this.movieSqlRepo = movieSqlRepo;
        this.personSqlRepo = personSqlRepo;
        this.personDocRepo = personDocRepo;
        this.companySqlRepo = companySqlRepo;
        this.companyDocRepo = companyDocRepo;
        this.reviewSqlRepo = reviewSqlRepo;
        this.reviewDocRepo = reviewDocRepo;
    }

    @Transactional
    public void migrate() {
        migrateMovies();
        migratePersons();
        migrateCompanies();
        migrateReviews();
        //migrateUsers();
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

    }

}


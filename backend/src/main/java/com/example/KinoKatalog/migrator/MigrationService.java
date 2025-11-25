package com.example.KinoKatalog.migrator;

import com.example.KinoKatalog.persistence.document.documents.MovieDocument;
import com.example.KinoKatalog.persistence.document.embedded.CastMember;
import com.example.KinoKatalog.persistence.document.embedded.CompanyInfo;
import com.example.KinoKatalog.persistence.document.embedded.CrewMember;
import com.example.KinoKatalog.persistence.document.repository.MovieDocumentRepository;
import com.example.KinoKatalog.persistence.sql.entity.GenreEntity;
import com.example.KinoKatalog.persistence.sql.entity.MovieEntity;
import com.example.KinoKatalog.persistence.sql.entity.PersonEntity;
import com.example.KinoKatalog.persistence.sql.entity.TagEntity;
import com.example.KinoKatalog.persistence.sql.repository.GenreSqlRepository;
import com.example.KinoKatalog.persistence.sql.repository.MovieSqlRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MigrationService {

    private final MovieDocumentRepository movieDocRepo;
    private final MovieSqlRepository movieSqlRepo;


    public MigrationService(MovieDocumentRepository movieDocRepo,
                            MovieSqlRepository movieSqlRepo) {
        this.movieDocRepo = movieDocRepo;
        this.movieSqlRepo = movieSqlRepo;
    }

    @Transactional
    public void migrate() {
        migrateMovies();

    }

    private void migrateMovies() {
        var movies = movieSqlRepo.findAll();

        for (var m : movies) {
            MovieDocument doc = MovieDocument.builder()
                    //.id(m.getId())
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

}


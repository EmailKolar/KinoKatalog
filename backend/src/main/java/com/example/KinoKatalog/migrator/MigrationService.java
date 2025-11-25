package com.example.KinoKatalog.migrator;

import com.example.KinoKatalog.persistence.document.documents.MovieDocument;
import com.example.KinoKatalog.persistence.document.repository.MovieDocumentRepository;
import com.example.KinoKatalog.persistence.sql.entity.MovieEntity;
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
        var movies = movieSqlRepo.findAll();

        for (var m : movies) {
            MovieDocument doc = MovieDocument.builder()
                    //.id(m.getId())
                    .title(m.getTitle())

                    .releaseDate(m.getReleaseDate())
                    .runtime(m.getRuntime())


                    .overview(m.getOverview())
                    .tmdbId(m.getTmdbId())

                    .build();

            movieDocRepo.save(doc);
            System.out.println("Migrated movie: " + m.getTitle());
        }
        }
    }


package com.example.KinoKatalog.migrator;

import com.example.KinoKatalog.persistence.document.documents.PersonDocument;
import com.example.KinoKatalog.persistence.document.repository.PersonDocumentRepository;
import com.example.KinoKatalog.persistence.sql.entity.PersonEntity;

import com.example.KinoKatalog.persistence.sql.repository.PersonSqlRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MigrationRunner implements CommandLineRunner {

    private final PersonSqlRepository personSqlRepo;
    private final PersonDocumentRepository personDocRepo;

    public MigrationRunner(PersonSqlRepository personSqlRepo,
                           PersonDocumentRepository personDocRepo) {
        this.personSqlRepo = personSqlRepo;
        this.personDocRepo = personDocRepo;
    }

    @Override
    public void run(String... args) {

        boolean dryRun = true;
        for (String arg : args) {
            if (arg.startsWith("--dryRun=")) {
                dryRun = Boolean.parseBoolean(arg.split("=")[1]);
            }
        }

        List<PersonEntity> persons = personSqlRepo.findAll();

        System.out.println("Found " + persons.size() + " SQL persons");

        List<PersonDocument> docs = persons.stream()
                .map(this::map)
                .toList();

        if (dryRun) {
            System.out.println("DRY RUN => would migrate " + docs.size() + " persons");
        } else {
            personDocRepo.saveAll(docs);
            System.out.println("Migrated " + docs.size() + " persons to MongoDB");
        }
    }

    private PersonDocument map(PersonEntity e) {
        return new PersonDocument(
                null,
                e.getTmdbId(),
                e.getName(),
                e.getBio(),
                e.getBirthDate() == null ? null : e.getBirthDate().toString()
        );
    }
}

package com.example.KinoKatalog.migrator;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MigrationSqlToNeoService {


    @Transactional
    public void migrate() {
        migrateMovies();
        migratePersons();
        migrateCompanies();
        migrateReviews();
        migrateUsers();
    }

    private void migrateUsers() {
    }

    private void migrateReviews() {
        
    }

    private void migrateCompanies() {
        
    }

    private void migratePersons() {
        
    }

    private void migrateMovies() {
        
    }


}

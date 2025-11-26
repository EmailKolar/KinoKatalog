package com.example.KinoKatalog.controller;

import com.example.KinoKatalog.migrator.MigrationService;
import com.example.KinoKatalog.migrator.MigrationSqlToNeoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin")
public class MigrationController {

    private final MigrationService migrationService;
    private final MigrationSqlToNeoService migrationSqlToNeoService;

    public MigrationController(MigrationService migrationService, MigrationSqlToNeoService migrationSqlToNeoService) {
        this.migrationService = migrationService;
        this.migrationSqlToNeoService = migrationSqlToNeoService;
    }

    @PostMapping("/run-mongo-migration")
    public ResponseEntity<String> runMigration(@RequestHeader("X-MIGRATION-KEY") String key) {

        if (!"SUPERSECRET".equals(key)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        migrationService.migrate();
        return ResponseEntity.ok("Mongo Migration done");
    }

    @PostMapping("/run-neo-migration")
    public ResponseEntity<String> runNeoMigration(@RequestHeader("X-MIGRATION-KEY") String key) {

        if (!"SUPERSECRET".equals(key)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        migrationSqlToNeoService.migrate();
        return ResponseEntity.ok("Neo4j Migration done");
    }



}

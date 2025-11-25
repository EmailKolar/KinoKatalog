package com.example.KinoKatalog.controller;

import com.example.KinoKatalog.migrator.MigrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class MigrationController {

    private final MigrationService migrationService;


    @PostMapping("/run-migration")
    public ResponseEntity<String> runMigration(@RequestHeader("X-MIGRATION-KEY") String key) {

        if (!"SUPERSECRET".equals(key)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        migrationService.migrate();
        return ResponseEntity.ok("Migration done");
    }
}

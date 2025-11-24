package com.example.KinoKatalog.migrator;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("migration")
@ComponentScan(
        basePackages = "com.example.KinoKatalog",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.example\\.KinoKatalog\\.controller.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.example\\.KinoKatalog\\.service.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.example\\.KinoKatalog\\.config.*")
        }
)
public class MigrationConfig {



}
package com.example.kinokatalog.config;

import org.neo4j.driver.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@Configuration
@EnableTransactionManagement
// JPA repositories (leave transactionManagerRef as default "transactionManager")
@EnableJpaRepositories(
        basePackages = "com.example.kinokatalog.persistence.sql.repository",
        // use default jpa transaction manager
        transactionManagerRef = "transactionManager"
)
// Mongo repositories (explicitly point to mongo repository package)
@EnableMongoRepositories(
        basePackages = "com.example.kinokatalog.persistence.document.repository",
        mongoTemplateRef = "mongoTemplate"

)
// Neo4j repositories (explicitly point to neo4j repo package and neo4j TM)
@EnableNeo4jRepositories(
        basePackages = "com.example.kinokatalog.persistence.graph.repository",
        transactionManagerRef = "neo4jTransactionManager",
        considerNestedRepositories = true
)
public class DataConfig {

    // ---- Neo4j transaction manager ----
    @Bean
    public Neo4jTransactionManager neo4jTransactionManager(Driver driver) {
        return new Neo4jTransactionManager(driver);
    }


    @Bean
    public MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        // Default JPA transaction manager
        return new org.springframework.orm.jpa.JpaTransactionManager();
    }
}

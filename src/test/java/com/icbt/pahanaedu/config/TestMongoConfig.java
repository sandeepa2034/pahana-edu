package com.icbt.pahanaedu.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@TestConfiguration
@Profile("test")
public class TestMongoConfig {

    @Bean
    @Primary
    public MongoClient mongoClient() {
        // Use a local MongoDB for tests or a mock
        return MongoClients.create("mongodb://localhost:27017");
    }

    @Bean
    @Primary
    public MongoDatabaseFactory mongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(mongoClient(), "test_db");
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDatabaseFactory());
    }

    @Bean
    @Primary
    public MongoTransactionManager transactionManager() {
        return new MongoTransactionManager(mongoDatabaseFactory());
    }
}

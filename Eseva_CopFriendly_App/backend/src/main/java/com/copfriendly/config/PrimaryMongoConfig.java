package com.copfriendly.config;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableMongoRepositories(
        basePackages = "com.copfriendly.repository"
)
public class PrimaryMongoConfig {

    private static final Logger log = LoggerFactory.getLogger(PrimaryMongoConfig.class);

    @Value("${spring.data.mongodb.uri}")
    private String primaryUri;

    @Bean
    @Primary
    public MongoClient primaryMongoClient() {
        if (primaryUri == null || primaryUri.isBlank()) {
            log.error("MongoDB URI is not set. Please set MONGO_URI or SPRING_DATA_MONGODB_URI environment variable.");
            throw new IllegalStateException("MongoDB URI is not configured");
        }
        // mask credentials for logging
        String masked = primaryUri.replaceAll("//(.*?):(.*?)@", "//****:****@");
        try {
            log.info("Creating primary MongoClient for URI: {}", masked);
        } catch (Exception ignored) {
            // logging should not break creation
        }
        return MongoClients.create(primaryUri);
    }

    @Bean(name = "mongoTemplate")
    @Primary
    public MongoTemplate mongoTemplate() {
        String database = new ConnectionString(primaryUri).getDatabase();
        if (database == null || database.isEmpty()) {
            log.warn("No database specified in Mongo URI; falling back to 'test'");
            database = "test"; // fallback
        }
        log.info("Using MongoDB database: {}", database);
        return new MongoTemplate(primaryMongoClient(), database);
    }
}

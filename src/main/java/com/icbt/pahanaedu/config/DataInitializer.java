package com.icbt.pahanaedu.config;

import com.icbt.pahanaedu.repository.UserRepository;
import com.icbt.pahanaedu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public void run(String... args) throws Exception {
        try {
            logger.info("🚀 Starting data initialization...");
            
            // Clean up users with null/empty phone numbers
            cleanupInvalidUsers();
            
            // Create default admin user if needed
            createDefaultAdminUser();
            
            // Ensure proper indexes
            ensureIndexes();
            
            logger.info("✅ Data initialization completed successfully!");
            
        } catch (Exception e) {
            logger.error("❌ Error during data initialization: " + e.getMessage(), e);
        }
    }
    
    private void cleanupInvalidUsers() {
        try {
            // Delete users with null phone
            Query nullPhoneQuery = Query.query(Criteria.where("phone").is(null));
            long deletedNull = mongoTemplate.remove(nullPhoneQuery, "users").getDeletedCount();
            logger.info("🗑️ Deleted {} users with null phone numbers", deletedNull);
            
            // Delete users with empty phone
            Query emptyPhoneQuery = Query.query(Criteria.where("phone").is(""));
            long deletedEmpty = mongoTemplate.remove(emptyPhoneQuery, "users").getDeletedCount();
            logger.info("🗑️ Deleted {} users with empty phone numbers", deletedEmpty);
            
        } catch (Exception e) {
            logger.warn("⚠️ Could not cleanup invalid users: " + e.getMessage());
        }
    }
    
    private void createDefaultAdminUser() {
        try {
            if (userRepository.countByRole("ADMIN") == 0) {
                userService.registerUser("admin", "+94771234567", "admin123", "ADMIN");
                logger.info("👑 Created default admin user");
            } else {
                logger.info("👑 Admin user already exists");
            }
        } catch (Exception e) {
            logger.error("❌ Could not create admin user: " + e.getMessage());
        }
    }
    
    private void ensureIndexes() {
        try {
            IndexOperations indexOps = mongoTemplate.indexOps("users");
            
            // Create sparse unique index on phone (ignores null/missing values)
            Index phoneIndex = new Index("phone", org.springframework.data.domain.Sort.Direction.ASC)
                    .unique()
                    .sparse();
            
            indexOps.ensureIndex(phoneIndex);
            logger.info("📋 Ensured sparse unique index on phone field");
            
        } catch (Exception e) {
            logger.warn("⚠️ Could not ensure indexes: " + e.getMessage());
        }
    }
}

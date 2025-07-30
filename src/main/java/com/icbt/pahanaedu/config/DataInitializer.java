package com.icbt.pahanaedu.config;

import com.icbt.pahanaedu.repository.UserRepository;
import com.icbt.pahanaedu.service.UserService;
import com.icbt.pahanaedu.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
    private CustomerService customerService;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public void run(String... args) throws Exception {
        try {
            logger.info("🚀 Starting data initialization...");
            
            // Clean up users with null/empty phone numbers
            cleanupInvalidUsers();
            
            // Clean up customers with null phone numbers
            cleanupInvalidCustomers();
            
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
    
    private void cleanupInvalidCustomers() {
        try {
            customerService.cleanupNullPhoneRecords();
            logger.info("🗑️ Cleaned up customers with null phone numbers");
        } catch (Exception e) {
            logger.warn("⚠️ Could not clean up customer phone records: " + e.getMessage());
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
            logger.info("🔧 Starting index management...");
            
            // Handle users collection indexes
            IndexOperations userIndexOps = mongoTemplate.indexOps("users");
            
            // Drop existing conflicting indexes first
            try {
                userIndexOps.dropIndex("phone_1");
                logger.info("🗑️ Dropped existing phone_1 index from users collection");
            } catch (Exception e) {
                logger.info("ℹ️ No existing phone_1 index to drop in users collection");
            }
            
            try {
                userIndexOps.dropIndex("phone");
                logger.info("🗑️ Dropped existing phone index from users collection");
            } catch (Exception e) {
                logger.info("ℹ️ No existing phone index to drop in users collection");
            }
            
            // Create new sparse unique index on phone
            Index userPhoneIndex = new Index("phone", org.springframework.data.domain.Sort.Direction.ASC)
                    .unique()
                    .sparse()
                    .named("users_phone_sparse_unique");
            
            userIndexOps.ensureIndex(userPhoneIndex);
            logger.info("📋 Created sparse unique index on users.phone field");
            
            // Handle customers collection indexes
            IndexOperations customerIndexOps = mongoTemplate.indexOps("customers");
            
            // Drop ALL existing conflicting indexes first (including old field names)
            try {
                customerIndexOps.dropIndex("phoneNumber_1");
                logger.info("🗑️ Dropped existing phoneNumber_1 index from customers collection");
            } catch (Exception e) {
                logger.info("ℹ️ No existing phoneNumber_1 index to drop in customers collection");
            }
            
            try {
                customerIndexOps.dropIndex("phone_1");
                logger.info("🗑️ Dropped existing phone_1 index from customers collection");
            } catch (Exception e) {
                logger.info("ℹ️ No existing phone_1 index to drop in customers collection");
            }
            
            try {
                customerIndexOps.dropIndex("phone");
                logger.info("🗑️ Dropped existing phone index from customers collection");
            } catch (Exception e) {
                logger.info("ℹ️ No existing phone index to drop in customers collection");
            }
            
            // Also try to drop any other variations of phone indexes
            try {
                customerIndexOps.dropIndex("phone_dup");
                logger.info("🗑️ Dropped existing phone_dup index from customers collection");
            } catch (Exception e) {
                logger.info("ℹ️ No existing phone_dup index to drop in customers collection");
            }
            
            // Remove any old documents that might have the old 'phone' field structure
            try {
                mongoTemplate.updateMulti(
                    Query.query(Criteria.where("phone").exists(true)),
                    new Update().unset("phone"),
                    "customers"
                );
                logger.info("🧹 Removed old 'phone' field from existing customer documents");
            } catch (Exception e) {
                logger.info("ℹ️ No old 'phone' field to remove from customer documents");
            }
            
            try {
                customerIndexOps.dropIndex("email_1");
                logger.info("🗑️ Dropped existing email_1 index from customers collection");
            } catch (Exception e) {
                logger.info("ℹ️ No existing email_1 index to drop in customers collection");
            }
            
            // Create new sparse unique indexes
            Index customerPhoneIndex = new Index("phoneNumber", org.springframework.data.domain.Sort.Direction.ASC)
                    .unique()
                    .sparse()
                    .named("customers_phone_sparse_unique");
            
            Index customerEmailIndex = new Index("email", org.springframework.data.domain.Sort.Direction.ASC)
                    .unique()
                    .sparse()
                    .named("customers_email_sparse_unique");
            
            customerIndexOps.ensureIndex(customerPhoneIndex);
            customerIndexOps.ensureIndex(customerEmailIndex);
            logger.info("📋 Created sparse unique indexes on customers.phoneNumber and customers.email fields");
            
        } catch (Exception e) {
            logger.warn("⚠️ Could not ensure indexes: " + e.getMessage());
        }
    }
}

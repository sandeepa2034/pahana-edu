package com.icbt.pahanaedu.repository;

import com.icbt.pahanaedu.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    /**
     * Find user by username (case-insensitive)
     * Used for authentication and user lookup
     */
    Optional<User> findByUsernameIgnoreCase(String username);
    
    /**
     * Check if username exists (case-insensitive)
     * Used for registration validation
     */
    boolean existsByUsernameIgnoreCase(String username);
    
    /**
     * Find all users by role
     * Useful for admin operations
     */
    List<User> findByRole(String role);
    
    /**
     * Find users by enabled status
     * For user management operations
     */
    List<User> findByEnabled(boolean enabled);
    
    /**
     * Count users by role
     * For dashboard statistics
     */
    long countByRole(String role);
    
    /**
     * Count total active users
     * For system monitoring
     */
    long countByEnabled(boolean enabled);
}
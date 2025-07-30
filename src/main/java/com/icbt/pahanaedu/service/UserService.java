package com.icbt.pahanaedu.service;

import com.icbt.pahanaedu.model.User;
import com.icbt.pahanaedu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Spring Security UserDetailsService implementation
     * Loads user by username for authentication
     */

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public UserDetails loadUserByUsername(String usernameOrPhone) throws UsernameNotFoundException {
        // Try to find by username first
        Optional<User> user = userRepository.findByUsernameIgnoreCase(usernameOrPhone);
        
        // If not found by username, try by phone
        if (user.isEmpty()) {
            user = userRepository.findByPhone(usernameOrPhone);
        }
        
        return user.orElseThrow(() -> 
            new UsernameNotFoundException("User not found with username or phone: " + usernameOrPhone));
    }
    
    /**
     * Register a new user with encrypted password
     */
    public User registerUser(String username, String phone, String password, String role) {
        System.out.println("=== USER REGISTRATION DEBUG ===");
        System.out.println("Username: " + username);
        System.out.println("Phone: " + phone);
        System.out.println("Role: " + role);
        
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username is empty");
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        if (phone == null || phone.trim().isEmpty()) {
            System.out.println("Phone is empty");
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        
        if (password == null || password.length() < 6) {
            System.out.println("Password too short: " + (password != null ? password.length() : "null"));
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        
        if (role == null || (!role.equals("ADMIN") && !role.equals("USER"))) {
            System.out.println("Invalid role: " + role);
            throw new IllegalArgumentException("Role must be either ADMIN or USER");
        }
        
        // Check if username already exists
        boolean usernameExists = userRepository.existsByUsernameIgnoreCase(username);
        System.out.println("Username exists check: " + usernameExists);
        if (usernameExists) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Check if phone already exists
        boolean phoneExists = userRepository.existsByPhone(phone);
        System.out.println("Phone exists check: " + phoneExists);
        if (phoneExists) {
            throw new IllegalArgumentException("Phone number already exists");
        }
        
        // Create new user with encrypted password
        User user = new User();
        user.setUsername(username.trim());
        user.setPhone(phone.trim());
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setEnabled(true);
        
        System.out.println("Saving user to database...");
        User savedUser = userRepository.save(user);
        System.out.println("User saved successfully with ID: " + savedUser.getId() + ", Username: " + savedUser.getUsername());
        return savedUser;
    }
    
    /**
     * Find user by username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }
    
    /**
     * Find user by phone number
     */
    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }
    
    /**
     * Check if username exists
     */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }
    
    /**
     * Get all users (for admin operations)
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get users by role
     */
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * Update user details
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    /**
     * Delete user by ID
     */
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
    
    /**
     * Enable or disable user account
     */
    public void toggleUserStatus(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEnabled(!user.isEnabled());
            userRepository.save(user);
        }
    }
    
    /**
     * Change user password
     */
    public void changePassword(String userId, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }
    
    /**
     * Get user statistics
     */
    public long getTotalUsers() {
        return userRepository.count();
    }
    
    public long getActiveUsers() {
        return userRepository.countByEnabled(true);
    }
    
    public long getAdminCount() {
        return userRepository.countByRole("ADMIN");
    }
    
    public long getUserCount() {
        return userRepository.countByRole("USER");
    }
}
package com.icbt.pahanaedu.controller;

import com.icbt.pahanaedu.model.User;
import com.icbt.pahanaedu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid; // ✅ use this instead

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Show login page
     */
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout,
                               @RequestParam(value = "expired", required = false) String expired,
                               Model model) {
        
        // Check if user is already authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        
        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
        }
        
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully!");
        }
        
        if (expired != null) {
            model.addAttribute("error", "Your session has expired. Please log in again.");
        }
        
        return "login";
    }
    
    /**
     * Show registration page
     */
    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        // Check if user is already authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        
        model.addAttribute("user", new User());
        return "register";
    }
    
    /**
     * Handle user registration
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                              BindingResult result,
                              @RequestParam("confirmPassword") String confirmPassword,
                              @RequestParam(value = "role", defaultValue = "USER") String role,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        // Validate form input
        if (result.hasErrors()) {
            return "register";
        }
        
        // Check if passwords match
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "register";
        }
        
        // Validate role
        if (!role.equals("ADMIN") && !role.equals("USER")) {
            role = "USER"; // Default to USER if invalid role
        }
        
        try {
            // Register the user
            userService.registerUser(user.getUsername(), user.getPassword(), role);
            
            redirectAttributes.addFlashAttribute("message", 
                "Registration successful! You can now log in with your credentials.");
            return "redirect:/login";
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed. Please try again.");
            return "register";
        }
    }
    
    /**
     * Dashboard - redirect based on user role
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);
        
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        
        // Redirect based on role
        if (user.getRole().equals("ADMIN")) {
            // Add admin-specific data
            model.addAttribute("totalUsers", userService.getTotalUsers());
            model.addAttribute("activeUsers", userService.getActiveUsers());
            model.addAttribute("adminCount", userService.getAdminCount());
            model.addAttribute("userCount", userService.getUserCount());
            
            return "admin/dashboard";
        } else {
            // User dashboard
            return "user/dashboard";
        }
    }
    
    /**
     * User profile page
     */
    @GetMapping("/profile")
    public String showProfile(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);
        
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "profile";
    }
    
    /**
     * Access denied page
     */
    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("error", "You don't have permission to access this resource.");
        return "error/access-denied";
    }
    
    /**
     * Check username availability (AJAX endpoint)
     */
    @GetMapping("/api/check-username")
    @ResponseBody
    public boolean checkUsernameAvailability(@RequestParam String username) {
        return !userService.usernameExists(username);
    }
    
    
}
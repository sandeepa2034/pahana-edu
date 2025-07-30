package com.icbt.pahanaedu.controller;

import com.icbt.pahanaedu.model.User;
import com.icbt.pahanaedu.model.Customer;
import com.icbt.pahanaedu.service.UserService;
import com.icbt.pahanaedu.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private CustomerService customerService;

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

        model.addAttribute("customer", new Customer());
        return "register";
    }

    /**
     * Handle user registration
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("customer") Customer customer,
            BindingResult result,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam(value = "role", defaultValue = "USER") String role,
            @RequestParam(value = "termsAccepted", defaultValue = "false") boolean termsAccepted,
            Model model,
            RedirectAttributes redirectAttributes) {

        System.out.println("=== REGISTRATION DEBUG ===");
        System.out.println("Username: " + username);
        System.out.println("Role: " + role);
        System.out.println("Phone: " + customer.getPhoneNumber());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("Terms accepted: " + termsAccepted);

        // Validate form input
        if (result.hasErrors()) {
            System.out.println("Validation errors found:");
            result.getAllErrors().forEach(error -> System.out.println(" - " + error.getDefaultMessage()));
            model.addAttribute("error", "Please fix the form errors and try again.");
            return "register";
        }

        // Check if terms are accepted
        if (!termsAccepted) {
            System.out.println("Terms not accepted");
            model.addAttribute("error", "You must accept the terms and conditions!");
            return "register";
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match");
            model.addAttribute("error", "Passwords do not match!");
            return "register";
        }

        // Validate role
        if (!role.equals("ADMIN") && !role.equals("USER")) {
            System.out.println("Invalid role: " + role + ", defaulting to USER");
            role = "USER"; // Default to USER if invalid role
        }

        try {
            System.out.println("Creating User account...");
            // Create User for authentication (using phone as phone field)
            User savedUser = userService.registerUser(username, customer.getPhoneNumber(), password, role);
            System.out.println("User created successfully with ID: " + savedUser.getId());

            System.out.println("Creating Customer profile...");
            // Create Customer for profile data
            Customer savedCustomer = customerService.createCustomer(customer);
            System.out.println("Customer created successfully with ID: " + savedCustomer.getId());

            System.out.println("Registration completed successfully!");
            redirectAttributes.addFlashAttribute("message",
                    "Registration successful! You can now log in with your credentials.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " + e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (RuntimeException e) {
            System.out.println("RuntimeException: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (Exception e) {
            System.out.println("Unexpected Exception: " + e.getMessage());
            e.printStackTrace();
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
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/user/dashboard";
        }
    }

    /**
     * User dashboard
     */
    @GetMapping("/user/dashboard")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String userDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("username", username);

        return "user/dashboard";
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
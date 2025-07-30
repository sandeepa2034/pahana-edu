package com.icbt.pahanaedu.controller;

import com.icbt.pahanaedu.model.Item;
import com.icbt.pahanaedu.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        // Initialize sample data if needed
        itemService.initializeSampleData();

        // Get featured items for landing page
        List<Item> featuredItems = itemService.getFeaturedItems();
        List<String> categories = itemService.getAllCategories();

        model.addAttribute("appName", "Pahana Edu Bookshop");
        model.addAttribute("message", "Welcome to Colombo's Premier Bookshop");
        model.addAttribute("featuredItems", featuredItems);
        model.addAttribute("categories", categories);
        
        // Add authentication context for Thymeleaf security
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", authentication.getName());
        } else {
            model.addAttribute("isAuthenticated", false);
        }
        
        return "index";
    }

    @GetMapping("/shop")
    public String shop(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            @RequestParam(value = "sort", defaultValue = "title") String sort,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model,
            Authentication authentication) {

        // Initialize sample data if needed
        itemService.initializeSampleData();

        List<Item> items;

        // Apply filters
        if (search != null && !search.trim().isEmpty()) {
            items = itemService.searchItems(search.trim());
        } else if (category != null && !category.trim().isEmpty()) {
            items = itemService.getItemsByCategory(category);
        } else if (minPrice != null && maxPrice != null) {
            items = itemService.getItemsByPriceRange(minPrice, maxPrice);
        } else {
            items = itemService.getAvailableItems();
        }

        // Get all categories for filter dropdown
        List<String> categories = itemService.getAllCategories();

        model.addAttribute("appName", "Pahana Edu Bookshop - Shop");
        model.addAttribute("items", items);
        model.addAttribute("categories", categories);
        model.addAttribute("currentCategory", category);
        model.addAttribute("currentSearch", search);
        model.addAttribute("currentMinPrice", minPrice);
        model.addAttribute("currentMaxPrice", maxPrice);

        // Add authentication context for Thymeleaf security
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", authentication.getName());
        } else {
            model.addAttribute("isAuthenticated", false);
        }

        return "shop";
    }

    @GetMapping("/help")
    public String help(Model model, Authentication authentication) {
        model.addAttribute("appName", "Pahana Edu Bookshop - Help & Support");
        
        // Add authentication context for Thymeleaf security
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", authentication.getName());
        } else {
            model.addAttribute("isAuthenticated", false);
        }
        
        return "help";
    }

    /**
     * Show shopping cart page
     */
    @GetMapping("/cart")
    public String cart(Model model, Authentication authentication) {
        model.addAttribute("appName", "Pahana Edu Bookshop - Shopping Cart");
        
        // Add authentication context for Thymeleaf security
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", authentication.getName());
        } else {
            model.addAttribute("isAuthenticated", false);
        }
        
        return "cart";
    }

    /**
     * Show checkout page
     */
    @GetMapping("/checkout")
    public String checkout(Model model) {
        model.addAttribute("appName", "Pahana Edu Bookshop - Checkout");
        return "checkout";
    }
}

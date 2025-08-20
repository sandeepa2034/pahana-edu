package com.icbt.pahanaedu.service;

import com.icbt.pahanaedu.model.Item;
import com.icbt.pahanaedu.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    // Get all items
    public List<Item> getAllItems() {
        return itemRepository.findAll(Sort.by(Sort.Direction.ASC, "title"));
    }

    // Get items with pagination
    public Page<Item> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    // Get item by ID
    public Optional<Item> getItemById(String id) {
        return itemRepository.findById(id);
    }

    // Get items by category
    public List<Item> getItemsByCategory(String category) {
        return itemRepository.findByCategoryIgnoreCase(category);
    }

    // Get available items only
    public List<Item> getAvailableItems() {
        return itemRepository.findByAvailableTrue();
    }

    // Search items by title or author
    public List<Item> searchItems(String searchTerm) {
        return itemRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(searchTerm, searchTerm);
    }

    // Get featured items (e.g., first 8 available items)
    public List<Item> getFeaturedItems() {
        return itemRepository.findTop8ByAvailableTrueOrderByTitleAsc();
    }

    // Get items by price range
    public List<Item> getItemsByPriceRange(Double minPrice, Double maxPrice) {
        return itemRepository.findByPriceBetweenAndAvailableTrue(minPrice, maxPrice);
    }

    // Save item
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    // Update item
    public Item updateItem(Item item) {
        return itemRepository.save(item);
    }

    // Delete item
    public void deleteItem(String id) {
        itemRepository.deleteById(id);
    }

    // Check if item exists
    public boolean existsById(String id) {
        return itemRepository.existsById(id);
    }

    // Get distinct categories
    public List<String> getAllCategories() {
        return itemRepository.findDistinctCategories();
    }

    // Initialize sample data if database is empty
    public void initializeSampleData() {
        if (itemRepository.count() == 0) {
            List<Item> sampleItems = List.of(
                    new Item("The Great Gatsby", "F. Scott Fitzgerald", "A classic American novel about the Jazz Age",
                            1500.00, "novels"),
                    new Item("To Kill a Mockingbird", "Harper Lee",
                            "A powerful story of racial injustice and childhood innocence", 1600.00, "novels"),
                    new Item("World War II: A Complete History", "Martin Gilbert",
                            "Comprehensive account of the Second World War", 2800.00, "history"),
                    new Item("The Story of Civilization", "Will Durant", "Epic historical narrative of human progress",
                            3200.00, "history"),
                    new Item("Dune", "Frank Herbert", "Epic science fiction saga set in the distant future", 2400.00,
                            "scifi"),
                    new Item("Foundation", "Isaac Asimov", "Classic science fiction about the fall of a galactic empire", 2200.00,
                            "scifi"),
                    new Item("Mathematics for Primary Schools", "Jane Wilson", "Fun and engaging math concepts for young learners",
                            1200.00, "educational"),
                    new Item("The Very Hungry Caterpillar", "Eric Carle",
                            "Beloved children's story about growth and transformation", 800.00, "children"));

            // Set additional properties for sample items
            for (int i = 0; i < sampleItems.size(); i++) {
                Item item = sampleItems.get(i);
                item.setIsbn("978-0-123456-" + String.format("%02d", i + 1) + "-0");
                item.setPublisher("Pahana Publishers");
                item.setPublishYear(2023 - (i % 3)); // Years 2021-2023
                item.setImageUrl("/images/books/book" + (i + 1) + ".jpg");
            }

            itemRepository.saveAll(sampleItems);
        }
        
        // Update existing items with old categories to new categories
        updateExistingCategories();
    }

    // Update existing items to use new category structure
    public void updateExistingCategories() {
        List<Item> allItems = itemRepository.findAll();
        boolean hasChanges = false;
        
        for (Item item : allItems) {
            String oldCategory = item.getCategory();
            String newCategory = null;
            
            // Map old categories to new ones
            switch (oldCategory.toLowerCase()) {
                case "fiction":
                case "programming":
                case "software engineering":
                    newCategory = "novels";
                    break;
                case "non-fiction":
                case "mathematics":
                case "physics":
                case "computer science":
                case "database":
                    newCategory = "educational";
                    break;
                case "textbook":
                case "textbooks":
                case "reference":
                    newCategory = "educational";
                    break;
                case "science fiction":
                case "sci-fi":
                    newCategory = "scifi";
                    break;
                case "children":
                case "kids":
                    newCategory = "children";
                    break;
                default:
                    // If it's already one of the new categories, keep it
                    if (oldCategory.equals("novels") || oldCategory.equals("history") || 
                        oldCategory.equals("scifi") || oldCategory.equals("educational") || 
                        oldCategory.equals("children")) {
                        newCategory = oldCategory;
                    } else {
                        // Default unmapped categories to educational
                        newCategory = "educational";
                    }
                    break;
            }
            
            if (!oldCategory.equals(newCategory)) {
                item.setCategory(newCategory);
                hasChanges = true;
            }
        }
        
        if (hasChanges) {
            itemRepository.saveAll(allItems);
        }
    }
}

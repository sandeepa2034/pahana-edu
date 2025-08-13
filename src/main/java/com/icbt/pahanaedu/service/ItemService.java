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
        if (item.getStock() != null) {
            item.setAvailable(item.getStock() > 0);
        }
        return itemRepository.save(item);
    }

    // Update item
    public Item updateItem(Item item) {
        if (item.getStock() != null) {
            item.setAvailable(item.getStock() > 0);
        }
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

    // Update stock when item is purchased
    public boolean updateStock(String itemId, int quantity) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            if (item.getStock() >= quantity) {
                item.setStock(item.getStock() - quantity);
                item.setAvailable(item.getStock() > 0);
                itemRepository.save(item);
                return true;
            }
        }
        return false;
    }

    // Get low stock items (stock <= 5)
    public List<Item> getLowStockItems() {
        return itemRepository.findByStockLessThanEqualAndAvailableTrue(5);
    }

    // Initialize sample data if database is empty
    public void initializeSampleData() {
        if (itemRepository.count() == 0) {
            List<Item> sampleItems = List.of(
                    new Item("Java Programming", "James Gosling", "Complete guide to Java programming language",
                            2500.00, 25, "Programming"),
                    new Item("Spring Boot in Action", "Craig Walls",
                            "Learn Spring Boot framework with practical examples", 3200.00, 15, "Programming"),
                    new Item("Data Structures and Algorithms", "Robert Sedgewick",
                            "Comprehensive guide to data structures and algorithms", 2800.00, 20, "Computer Science"),
                    new Item("MongoDB: The Definitive Guide", "Shannon Bradshaw", "Master MongoDB database development",
                            2900.00, 12, "Database"),
                    new Item("Clean Code", "Robert C. Martin", "A handbook of agile software craftsmanship", 2400.00,
                            18, "Software Engineering"),
                    new Item("The Pragmatic Programmer", "David Thomas", "Your journey to mastery", 2600.00, 22,
                            "Software Engineering"),
                    new Item("Introduction to Mathematics", "John Smith", "Fundamental concepts in mathematics",
                            1800.00, 30, "Mathematics"),
                    new Item("Physics for Engineers", "Mary Johnson",
                            "Applied physics concepts for engineering students", 3500.00, 8, "Physics"));

            // Set additional properties for sample items
            for (int i = 0; i < sampleItems.size(); i++) {
                Item item = sampleItems.get(i);
                item.setIsbn("978-0-123456-" + String.format("%02d", i + 1) + "-0");
                item.setPublisher("Academic Press");
                item.setPublishYear(2023 - (i % 3)); // Years 2021-2023
                item.setImageUrl("/images/books/book" + (i + 1) + ".jpg");
            }

            itemRepository.saveAll(sampleItems);
        }
    }
}

package com.icbt.pahanaedu.repository;

import com.icbt.pahanaedu.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends MongoRepository<Item, String> {

    // Find by category (case insensitive)
    List<Item> findByCategoryIgnoreCase(String category);

    // Find available items only
    List<Item> findByAvailableTrue();

    // Search by title or author (case insensitive)
    List<Item> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);

    // Find by price range and available
    List<Item> findByPriceBetweenAndAvailableTrue(Double minPrice, Double maxPrice);

    // Find top 8 available items ordered by title
    List<Item> findTop8ByAvailableTrueOrderByTitleAsc();

    // Find by ISBN
    Item findByIsbn(String isbn);

    // Find by author
    List<Item> findByAuthorIgnoreCase(String author);

    // Find by publisher
    List<Item> findByPublisherIgnoreCase(String publisher);

    // Get distinct categories
    @Query(value = "{}", fields = "{ 'category' : 1}")
    List<String> findDistinctCategories();

    // Custom query to find books by multiple criteria
    @Query("{ $and: [ " +
           "{ $or: [ " +
           "  { 'title': { $regex: ?0, $options: 'i' } }, " +
           "  { 'author': { $regex: ?0, $options: 'i' } }, " +
           "  { 'description': { $regex: ?0, $options: 'i' } } " +
           "] }, " +
           "{ 'available': true } " +
           "] }")
    List<Item> searchAvailableItems(String searchTerm);

    // Find by category and price range
    List<Item> findByCategoryIgnoreCaseAndPriceBetweenAndAvailableTrue(String category, Double minPrice, Double maxPrice);

    // Count available items by category
    @Query(value = "{ 'category': ?0, 'available': true }", count = true)
    long countByCategoryAndAvailable(String category);
}

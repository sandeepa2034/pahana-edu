package com.icbt.pahanaedu.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Item model class
 */
class ItemTest {

    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item("Test Book", "Test Author", "Test Description", 25.99, "educational");
    }

    @Test
    void testItemCreation() {
        assertNotNull(item);
        assertEquals("Test Book", item.getTitle());
        assertEquals("Test Author", item.getAuthor());
        assertEquals("Test Description", item.getDescription());
        assertEquals(25.99, item.getPrice());
        assertEquals("educational", item.getCategory());
        assertTrue(item.getAvailable());
    }

    @Test
    void testFormattedPrice() {
        String formattedPrice = item.getFormattedPrice();
        assertEquals("Rs. 25.99", formattedPrice);
    }

    @Test
    void testToString() {
        String toString = item.toString();
        assertTrue(toString.contains("Test Book"));
        assertTrue(toString.contains("Test Author"));
        assertTrue(toString.contains("25.99"));
        assertTrue(toString.contains("educational"));
    }
}

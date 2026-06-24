package org.yearup.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.yearup.models.Product;
import org.yearup.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository productRepository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = Mockito.mock(ProductRepository.class);
        productService = new ProductService(productRepository);
    }

    // BUG 1: Search Logic Tests

    @Test
    void search_ReturnsAllProducts_WhenNoFiltersApplied() {
        // Arrange
        Product p1 = new Product(1, "Basic Tee", 15.00, 1, "Cotton tee", "Shirts", 50, false, "tee.png");
        Product p2 = new Product(2, "Jeans", 45.00, 2, "Denim jeans", "Pants", 30, true, "jeans.png");

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        // Act
        List<Product> result = productService.search(null, null, null, null);

        // Assert
        assertEquals(2, result.size(), "Should return all products when all parameters are null.");
        verify(productRepository).findAll();
    }

    @Test
    void search_FiltersByPriceAndSubCategory_Properly() {
        // Arrange
        Product cheap = new Product(1, "Socks", 5.00, 3, "Warm socks", "Accessories", 100, false, "socks.png");
        Product expensive = new Product(2, "Leather Jacket", 200.00, 4, "Real leather", "Outerwear", 10, true, "jacket.png");
        Product wrongCategory = new Product(3, "Belt", 25.00, 3, "Leather belt", "Belts", 40, false, "belt.png");

        when(productRepository.findAll()).thenReturn(List.of(cheap, expensive, wrongCategory));

        // Act
        List<Product> result = productService.search(null, 1.00, 50.00, "Accessories");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Socks", result.get(0).getName());
    }

    // BUG 2: Update Logic Test (Stock Fix)

    @Test
    void update_SuccessfullyUpdatesStock_AndOtherFields() {
        // Arrange

        Product dbProduct = new Product(1, "Sneakers", 60.00, 5, "Running shoes", "Footwear", 5, false, "shoes.png");
        Product incomingData = new Product(1, "Sneakers V2", 70.00, 5, "Updated running shoes", "Footwear", 100, true, "shoes_v2.png");

        when(productRepository.findById(1)).thenReturn(Optional.of(dbProduct));
        when(productRepository.save(any(Product.class))).thenReturn(dbProduct);

        // Act
        productService.update(1, incomingData);

        // Assert
        assertEquals(100, dbProduct.getStock(), "Stock must be updated to the incoming value.");
        assertEquals("Sneakers V2", dbProduct.getName());
        assertEquals(70.00, dbProduct.getPrice());
        assertTrue(dbProduct.isFeatured());
        verify(productRepository).save(dbProduct);
    }


    @Test
    void create_SavesProduct_WithIdZero() {
        // Arrange
        Product newProduct = new Product(99, "Beanie", 12.00, 3, "Winter hat", "Accessories", 20, false, "beanie.png");
        when(productRepository.save(newProduct)).thenReturn(newProduct);

        // Act
        productService.create(newProduct);

        // Assert
        assertEquals(0, newProduct.getProductId(), "Create should reset ID to 0 before saving.");
        verify(productRepository).save(newProduct);
    }

    @Test
    void getById_ReturnsProduct_WhenExists() {
        // Arrange
        Product p = new Product(10, "Hoodie", 35.00, 1, "Warm hoodie", "Sweaters", 15, true, "hoodie.png");
        when(productRepository.findById(10)).thenReturn(Optional.of(p));

        // Act
        Product result = productService.getById(10);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getProductId());
        assertEquals("Hoodie", result.getName());
    }

    @Test
    void delete_CallsRepositoryDelete() {
        // Act
        productService.delete(1);

        // Assert
        verify(productRepository).deleteById(1);
    }
}
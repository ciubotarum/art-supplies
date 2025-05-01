package com.onlinestore.art_supplies.products;

import com.onlinestore.art_supplies.category.Category;
import com.onlinestore.art_supplies.category.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    ProductService productService;

    @Test
    public void shouldReturnAllTheProducts() {
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setProductName("Test Product 1");

        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductName("Test Product 2");

        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        List<Product> products = productService.getAllProducts();
        assertEquals(2, products.size());
        assertEquals("Test Product 1", products.get(0).getProductName());
        assertEquals("Test Product 2", products.get(1).getProductName());
    }

    @Test
    public void shouldReturnAnEmptyListWhenNoProductsFound() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<Product> products = productService.getAllProducts();
        assertTrue(products.isEmpty());
    }

    @Test
    public void shouldReturnAProductById() {
        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("Test Product");

        when(productRepository.findById(1L)).thenReturn(java.util.Optional.of(product));

        Product foundProduct = productService.getProductById(1L);
        assertNotNull(foundProduct);
        assertEquals("Test Product", foundProduct.getProductName());
    }

    @Test
    public void shouldReturnNullWhenProductNotFoundById() {
        when(productRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        Product foundProduct = productService.getProductById(1L);
        assertNull(foundProduct);
    }

    @Test
    public void shouldAddAProductIfCategoryExists() {
        Product product = new Product();
        product.setProductName("Test Product");

        Category category = new Category();
        category.setCategoryId(1L);
        product.setCategory(category);

        when(productRepository.save(product)).thenReturn(product);
        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(category));

        Product savedProduct = productService.addProduct(product);
        assertNotNull(savedProduct);
        assertEquals("Test Product", savedProduct.getProductName());
    }

    @Test
    public void shouldThrowExceptionWhenCategoryNotFound() {
        Product product = new Product();
        Category category = new Category();
        category.setCategoryId(99L);
        product.setCategory(category);

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productService.addProduct(product);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Category not found"));

        verify(productRepository, never()).save(any());

    }

    @Test
    public void shouldDeleteAProductById() {
        Long productId = 1L;

        when(productRepository.existsById(productId)).thenReturn(true);

        productService.deleteProduct(productId);
    }

    @Test
    public void shouldThrowExceptionWhenProductNotFoundForDeletion() {
        Long productId = 1L;

        when(productRepository.existsById(productId)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> {
            productService.deleteProduct(productId);
        });
    }

    @Test
    public void shouldReturnProductsWhenKeywordIsProvided() {
        String keyword = "Test";
        Product product1 = new Product();
        product1.setProductName("Test Product 1");
        Product product2 = new Product();
        product2.setProductName("Test Product 2");

        when(productRepository.searchProducts(keyword)).thenReturn(List.of(product1, product2));

        List<Product> products = productService.searchProducts(keyword);
        assertEquals(2, products.size());
    }

    @Test
    public void shouldReturnAnEmptyListWhenNoKeywordProvided() {
        List<Product> resultWithNull = productService.searchProducts(null);
        List<Product> resultWithEmpty = productService.searchProducts("");
        List<Product> resultWithSpaces = productService.searchProducts("   ");

        assertTrue(resultWithNull.isEmpty(), "Expected empty list when keyword is null");
        assertTrue(resultWithEmpty.isEmpty(), "Expected empty list when keyword is empty");
        assertTrue(resultWithSpaces.isEmpty(), "Expected empty list when keyword is only spaces");

        verify(productRepository, never()).searchProducts(anyString());
    }

    @Test
    public void shouldReturnTrueIfProductExistsById() {
        Long productId = 1L;

        when(productRepository.existsById(productId)).thenReturn(true);

        boolean exists = productService.productExistsById(productId);
        assertTrue(exists);
    }

    @Test
    public void shouldReturnFalseIfProductDoesNotExistById() {
        Long productId = 1L;

        when(productRepository.existsById(productId)).thenReturn(false);

        boolean exists = productService.productExistsById(productId);
        assertFalse(exists);
    }

    @Test
    public void shouldUpdateAProductIfFound() {
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setProductId(productId);
        existingProduct.setProductName("Old Product");

        Product updatedProduct = new Product();
        updatedProduct.setProductName("Updated Product");

        when(productRepository.findById(productId)).thenReturn(java.util.Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);

        Product result = productService.updateProduct(productId, updatedProduct);
        assertNotNull(result);
        assertEquals("Updated Product", result.getProductName());
    }

    @Test
    public void shouldThrowExceptionWhenProductNotFoundForUpdate() {
        Long productId = 1L;
        Product updatedProduct = new Product();
        updatedProduct.setProductName("Updated Product");

        when(productRepository.findById(productId)).thenReturn(java.util.Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            productService.updateProduct(productId, updatedProduct);
        });
    }
}
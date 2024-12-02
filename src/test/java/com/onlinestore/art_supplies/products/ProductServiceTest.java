package com.onlinestore.art_supplies.products;

import com.onlinestore.art_supplies.category.Category;
import com.onlinestore.art_supplies.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProducts() {
        Product product = new Product();
        Product product2 = new Product();

        when(productRepository.findAll()).thenReturn(List.of(product, product2));

        List<Product> products = productService.getAllProducts();

        assertEquals(2, products.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductById() {
        Product product = new Product();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product foundProduct = productService.getProductById(1L);

        assertNotNull(foundProduct);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testAddProduct() {
        Product product = new Product();
        Category category = new Category();
        category.setCategoryId(1L);
        product.setCategory(category);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product savedProduct = productService.addProduct(product);

        assertNotNull(savedProduct);
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product);

    }

    @Test
    void testAddProductCategoryNotFound() {
        Product product = new Product();
        Category category = new Category();
        category.setCategoryId(1L);
        product.setCategory(category);

        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> productService.addProduct(product));
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, times(0)).save(any(Product.class));
    }

    @Test
    void testDeleteProduct() {
        productService.deleteProduct(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testSearchProducts() {
        Product product = new Product();
        when(productRepository.searchProducts("keyword")).thenReturn(Arrays.asList(product));

        List<Product> products = productService.searchProducts("keyword");

        assertEquals(1, products.size());
        verify(productRepository, times(1)).searchProducts("keyword");
    }

    @Test
    void testUpdateProduct() {
        Product existingProduct = new Product();
        Product updatedProduct = new Product();
        updatedProduct.setProductName("Updated Name");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(1L, updatedProduct);

        assertEquals("Updated Name", result.getProductName());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    void testUpdateProductNotFound() {
        Product updatedProduct = new Product();

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> productService.updateProduct(1L, updatedProduct));
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(0)).save(any(Product.class));
    }

    @Test
    void testGetProductsByCategoryName() {
        Product product1 = new Product();
        Product product2 = new Product();

        when(productRepository.findByCategoryName("Watercolor")).thenReturn(Arrays.asList(product1, product2));

        List<Product> products = productService.getProductsByCategoryName("Watercolor");

        assertEquals(2, products.size());
        verify(productRepository, times(1)).findByCategoryName("Watercolor");
    }
}
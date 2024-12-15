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

    private Product product;
    private Product product2;
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product();
        product2 = new Product();
        category = new Category();
        category.setCategoryId(1L);
        product.setCategory(category);
    }

    @Test
    void testGetAllProducts() {

        when(productRepository.findAll()).thenReturn(List.of(product, product2));

        List<Product> products = productService.getAllProducts();

        assertEquals(2, products.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductById() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product foundProduct = productService.getProductById(1L);

        assertNotNull(foundProduct);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testAddProduct() {

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product savedProduct = productService.addProduct(product);

        assertNotNull(savedProduct);
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product);

    }

    @Test
    void testAddProductCategoryNotFound() {

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
        when(productRepository.searchProducts("keyword")).thenReturn(Arrays.asList(product));

        List<Product> products = productService.searchProducts("keyword");

        assertEquals(1, products.size());
        verify(productRepository, times(1)).searchProducts("keyword");
    }

    @Test
    void testUpdateProduct() {
        Product updatedProduct = new Product();
        updatedProduct.setProductName("Updated Name");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(1L, updatedProduct);

        assertEquals("Updated Name", result.getProductName());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product);
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

        when(productRepository.findByCategoryName("Watercolor")).thenReturn(Arrays.asList(product, product2));

        List<Product> products = productService.getProductsByCategoryName("Watercolor");

        assertEquals(2, products.size());
        verify(productRepository, times(1)).findByCategoryName("Watercolor");
    }
}
package com.onlinestore.art_supplies.products;

import com.onlinestore.art_supplies.category.Category;
import com.onlinestore.art_supplies.category.CategoryService;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private UserService userService;

    @MockBean
    private CategoryService categoryService;

    private User adminUser;
    private Category category;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminUser = new User();
        adminUser.setIsAdmin(true);
        adminUser.setUserId(1L);

        category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Watercolor");

        product1 = new Product();
        product1.setProductId(1L);
        product1.setProductName("Paint Brush");
        product1.setDescription("A high-quality brush.");
        product1.setPrice(BigDecimal.valueOf(15.99));
        product1.setQuantity(50);
        product1.setImage("http://example.com/image.jpg");

        product2 = new Product();
    }

    @Test
    void testGetAllProducts_ShouldReturnOkStatus() throws Exception {
        when(productService.getAllProducts()).thenReturn(Arrays.asList(product1, product2));

        mockMvc.perform(MockMvcRequestBuilders.get("/products/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
    }

    @Test
    void testGetProductById_ShouldReturnOkStatus() throws Exception {
        when(productService.getProductById(1L)).thenReturn(product1);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/product/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(1L));
    }


    @Test
    void testGetProductById_NotFound() throws Exception {
        when(productService.getProductById(1L)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/product/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testAddProduct_Success() throws Exception {

        when(userService.getUserById(1L)).thenReturn(Optional.of(adminUser));
        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(productService.addProduct(any(Product.class))).thenReturn(product1);

        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .param("adminId", "1")
                        .param("categoryId", "1")
                        .content("""
                                {
                                    "productName": "Paint Brush",
                                    "description": "A high-quality brush.",
                                    "price": 15.99,
                                    "quantity": 50,
                                    "image": "http://example.com/image.jpg"
                                }
                                """)
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").value("Paint Brush"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("A high-quality brush."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(15.99))
                .andExpect(MockMvcResultMatchers.jsonPath("$.quantity").value(50))
                .andExpect(MockMvcResultMatchers.jsonPath("$.image").value("http://example.com/image.jpg"));
    }

    @Test
    void testAddProduct_InvalidUrl() throws Exception {

        String invalidProductJson = """
                    {
                        "productName": "Test Product",
                        "description": "A test product",
                        "price": 10.99,
                        "quantity": 10,
                        "category": {
                            "categoryId": 1,
                            "categoryName": "Watercolor"
                        },
                        "image": "invalid-url"
                    }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .param("adminId", "1")
                        .param("categoryId", "1")
                        .content(invalidProductJson)
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Failed validations: Image must be a valid URL."));
    }

    @Test
    void testAddProduct_NotAdmin() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Only logged-in admins can perform this action."))
                .when(userService).checkAdminAndLoggedIn(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .param("adminId", "1")
                        .param("categoryId", "1")
                        .content("""
                                {
                                    "productName": "Paint Brush",
                                    "description": "A high-quality brush.",
                                    "price": 15.99,
                                    "quantity": 50,
                                    "image": "http://example.com/image.jpg"
                                }
                                """)
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access denied: Only logged-in admins can perform this action."));

    }

    @Test
    void testDeleteProduct_Success() throws Exception {

        when(userService.getUserById(anyLong())).thenReturn(Optional.of(adminUser));
        when(productService.productExistsById(anyLong())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/products")
                        .param("productId", "1")
                        .param("adminId", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Product deleted successfully!"));
    }

    @Test
    void testDeleteProduct_NotFound() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(adminUser));
        when(productService.productExistsById(anyLong())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/products")
                        .param("productId", "1")
                        .param("adminId", "1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("No product found with the given ID."));
    }

    @Test
    void testDeleteProduct_NotAdmin() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Only logged-in admins can perform this action."))
                .when(userService).checkAdminAndLoggedIn(anyLong());
        when(productService.productExistsById(anyLong())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/products")
                        .param("productId", "1")
                        .param("adminId", "1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access denied: Only logged-in admins can perform this action."));
    }

    @Test
    void testSearchProducts() throws Exception {

        when(productService.searchProducts("keyword")).thenReturn(Arrays.asList(product1));

        mockMvc.perform(MockMvcRequestBuilders.get("/products/search")
                        .param("keyword", "keyword"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testUpdateProduct_Success() throws Exception {

        Product updatedProduct = new Product();
        updatedProduct.setProductId(1L);
        updatedProduct.setProductName("Updated Name");
        updatedProduct.setDescription("A high-quality brush.");
        updatedProduct.setPrice(BigDecimal.valueOf(15.99));
        updatedProduct.setQuantity(50);
        updatedProduct.setImage("http://example.com/image.jpg");

        doNothing().when(userService).checkAdminAndLoggedIn(anyLong());
        when(productService.productExistsById(anyLong())).thenReturn(true);
        when(productService.updateProduct(anyLong(), any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(MockMvcRequestBuilders.put("/products/1")
                        .param("adminId", "1")
                        .content("""
                                {
                                    "productName": "Updated Name",
                                    "description": "A high-quality brush.",
                                    "price": 15.99,
                                    "quantity": 50,
                                    "image": "http://example.com/image.jpg"
                                }
                                """)
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").value("Updated Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("A high-quality brush."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(15.99))
                .andExpect(MockMvcResultMatchers.jsonPath("$.quantity").value(50))
                .andExpect(MockMvcResultMatchers.jsonPath("$.image").value("http://example.com/image.jpg"));
    }

    @Test
    void testUpdateProduct_NotAdmin() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Only logged-in admins can perform this action."))
                .when(userService).checkAdminAndLoggedIn(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.put("/products/1")
                        .param("adminId", "1")
                        .content("""
                                {
                                    "productName": "Updated Name",
                                    "description": "A high-quality brush.",
                                    "price": 15.99,
                                    "quantity": 50,
                                    "image": "http://example.com/image.jpg"
                                }
                                """)
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access denied: Only logged-in admins can perform this action."));
    }

    @Test
    void testFilterProductsByCategory_Success() throws Exception {

        when(productService.getProductsByCategoryName("Watercolor")).thenReturn(Arrays.asList(product1, product2));

        mockMvc.perform(MockMvcRequestBuilders.get("/products/filter")
                        .param("categoryName", "Watercolor"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
    }

    @Test
    void testFilterProductsByCategory_NotFound() throws Exception {
        when(productService.getProductsByCategoryName("NonExistentCategory")).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/products/filter")
                        .param("categoryName", "NonExistentCategory"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("No such category"));
    }
}
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

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


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProducts() throws Exception {
        Product product1 = new Product();
        Product product2 = new Product();
        when(productService.getAllProducts()).thenReturn(Arrays.asList(product1, product2));

        mockMvc.perform(MockMvcRequestBuilders.get("/products/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
    }

    @Test
    void testGetProductById() throws Exception {
        Product product = new Product();
        product.setProductId(1L);
        when(productService.getProductById(1L)).thenReturn(product);

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
        User adminUser = new User();
        adminUser.setIsAdmin(true);
        adminUser.setUserId(1L);

        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Watercolor");

        Product product = new Product();
        product.setImage("http://example.com/image.jpg");
        product.setProductName("Paint Brush");
        product.setDescription("A high-quality brush.");
        product.setPrice(BigDecimal.valueOf(15.99));
        product.setQuantity(50);

        when(userService.getUserById(1L)).thenReturn(Optional.of(adminUser));
        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(productService.addProduct(any(Product.class))).thenReturn(product);

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
        User user = new User();

        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));

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
                .andExpect(MockMvcResultMatchers.content().string("Access denied: Only admins can add a product."));

    }

    @Test
    void testDeleteProduct_Success() throws Exception {
        User adminUser = new User();
        adminUser.setIsAdmin(true);

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
        User adminUser = new User();
        adminUser.setIsAdmin(true);

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
        User user = new User();
        Product product = new Product();
        product.setProductId(1L);

        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(productService.productExistsById(anyLong())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/products")
                        .param("productId", "1")
                        .param("adminId", "1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string("Only admin can delete a product."));
    }

    @Test
    void testSearchProducts() throws Exception {
        Product product = new Product();
        when(productService.searchProducts("keyword")).thenReturn(Arrays.asList(product));

        mockMvc.perform(MockMvcRequestBuilders.get("/products/search")
                        .param("keyword", "keyword"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testUpdateProduct_Success() throws Exception {
        User adminUser = new User();
        adminUser.setIsAdmin(true);

        Product initialProduct = new Product();
        initialProduct.setProductId(1L);
        initialProduct.setProductName("Original Name");
        initialProduct.setDescription("A high-quality brush.");
        initialProduct.setPrice(BigDecimal.valueOf(15.99));
        initialProduct.setQuantity(50);
        initialProduct.setImage("http://example.com/image.jpg");

        Product updatedProduct = new Product();
        updatedProduct.setProductId(1L);
        updatedProduct.setProductName("Updated Name");
        updatedProduct.setDescription("A high-quality brush.");
        updatedProduct.setPrice(BigDecimal.valueOf(15.99));
        updatedProduct.setQuantity(50);
        updatedProduct.setImage("http://example.com/image.jpg");


        when(userService.getUserById(anyLong())).thenReturn(Optional.of(adminUser));
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
        User user = new User();
        Product updatedProduct = new Product();

        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));

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
                .andExpect(MockMvcResultMatchers.content().string("Only admin can delete a product."));
    }

    @Test
    void testFilterProductsByType() throws Exception {
        Product product = new Product();
        Product product2 = new Product();

        when(productService.getProductsByCategoryName("category")).thenReturn(Arrays.asList(product, product2));

        mockMvc.perform(MockMvcRequestBuilders.get("/products/filter")
                        .param("categoryName", "category"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));

    }
}
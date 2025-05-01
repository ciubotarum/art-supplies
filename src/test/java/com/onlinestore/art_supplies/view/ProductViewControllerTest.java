package com.onlinestore.art_supplies.view;

import com.onlinestore.art_supplies.category.Category;
import com.onlinestore.art_supplies.category.CategoryService;
import com.onlinestore.art_supplies.products.Product;
import com.onlinestore.art_supplies.products.ProductService;
import com.onlinestore.art_supplies.ratings.RatingService;
import com.onlinestore.art_supplies.reviews.Review;
import com.onlinestore.art_supplies.reviews.ReviewService;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Profile("h2")
class ProductViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryService categoryService;

//    @MockBean
//    Model model;

    @MockBean
    private UserService userService;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private RatingService ratingService;

    @Test
    public void shouldReturnListOfProducts() throws Exception {
        Category category = new Category();
        category.setCategoryName("Test Category");

        Product product = new Product();
        product.setProductName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(10.99));
        product.setQuantity(5);
        product.setCategory(category);
        product.setImage("http://example.com/image.jpg");

        Page<Product> productPage = new PageImpl<>(List.of(product));
        List<Category> categories = List.of(category);

        when(productService.getFilteredProducts(0, null, null)).thenReturn(productPage);
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/products/show")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attribute("products", productPage.getContent()))
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("totalPages", productPage.getTotalPages()))
                .andExpect(model().attribute("categories", categories));

        verify(productService).getFilteredProducts(0, null, null);
        verify(categoryService).getAllCategories();
    }

    @Test
    public void shouldReturnOnlyTheProductsWithTheSameCategoryName() throws Exception {
        String categoryName = "Test Category";
        Category category = new Category();
        category.setCategoryName(categoryName);

        Category category2 = new Category();
        category2.setCategoryName("Another Category");

        Product product = new Product();
        product.setProductName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(10.99));
        product.setQuantity(5);
        product.setCategory(category);
        product.setImage("http://example.com/image.jpg");

        Product product2 = new Product();
        product2.setProductName("Another Product");
        product2.setDescription("Another Description");
        product2.setPrice(BigDecimal.valueOf(20.99));
        product2.setQuantity(3);
        product2.setCategory(category2);
        product2.setImage("http://example.com/another_image.jpg");

        Page<Product> productPage = new PageImpl<>(List.of(product));
        List<Category> categories = List.of(category);

        when(productService.getFilteredProducts(0, null, categoryName)).thenReturn(productPage);
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/products/show")
                        .param("page", "0")
                        .param("categoryName", categoryName))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attribute("products", productPage.getContent()))
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("totalPages", productPage.getTotalPages()))
                .andExpect(model().attribute("categories", categories));

        verify(productService).getFilteredProducts(0, null, categoryName);
        verify(categoryService).getAllCategories();
    }

    @Test
    public void shouldReturnTheProductsWithTheGivenId() throws Exception {
        Product product = new Product();
        product.setProductName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(10.99));
        product.setQuantity(5);
        product.setImage("http://example.com/image.jpg");

        User user = new User();
        user.setUsername("testuser");
        user.setUserId(1L);

        Review review = new Review();
        review.setReviewText("Great product!");
        review.setUser(user);
        review.setReviewId(1L);

        List<Review> reviews = List.of(review);

        when(productService.getProductById(1L)).thenReturn(product);
        when(userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(user);
        when(ratingService.getAverageRating(1L)).thenReturn(4.5);
        when(reviewService.getReviewsByProductId(1L)).thenReturn(reviews);
        when(reviewService.canUserEditReview(user, 1L)).thenReturn(false);
        when(reviewService.canUserReviewProduct(user, 1L)).thenReturn(false);
        when(ratingService.canUserRateProduct(user, 1L)).thenReturn(false);

        mockMvc.perform(get("/products/show/{productId}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("product-details"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attributeExists("reviews"))
                .andExpect(model().attributeExists("canReview"))
                .andExpect(model().attributeExists("averageRating"))
                .andExpect(model().attributeExists("canRate"))
                .andExpect(model().attributeExists("canEditMap"))
                .andExpect(model().attributeExists("canDeleteMap"))
                .andExpect(model().attribute("product", product));

        verify(productService).getProductById(1L);
        verify(userService).getAuthenticatedUser(any(HttpServletRequest.class));
        verify(ratingService).getAverageRating(1L);
        verify(reviewService).getReviewsByProductId(1L);
        verify(reviewService).canUserEditReview(user, 1L);
        verify(reviewService).canUserReviewProduct(user, 1L);
        verify(ratingService).canUserRateProduct(user, 1L);

    }

    @Test
    public void shouldReturnFalseForCanEditIfUserNotCreatedReview() {
        User user = new User();
        user.setUsername("testuser");
        user.setUserId(1L);

        Review review = new Review();
        review.setReviewText("Great product!");
        review.setUser(user);
        review.setReviewId(1L);

        when(reviewService.canUserEditReview(user, 1L)).thenReturn(false);

        boolean canEdit = reviewService.canUserEditReview(user, 1L);

        assertFalse(canEdit);
    }

    @Test
    public void shouldReturnFalseForCanCreateReviewIfUserNotOrderedThatProduct() {
        User user = new User();
        user.setUsername("testuser");
        user.setUserId(1L);

        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("Test Product");

        when(reviewService.canUserReviewProduct(user, 1L)).thenReturn(false);

        boolean canReview = reviewService.canUserReviewProduct(user, 1L);

        assertFalse(canReview);
    }

    @Test
    public void shouldReturnTrueForCarCreateReviewIfUserOrderedTheProduct() {
        User user = new User();
        user.setUsername("testuser");
        user.setUserId(1L);

        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("Test Product");

        when(reviewService.canUserReviewProduct(user, 1L)).thenReturn(true);

        boolean canReview = reviewService.canUserReviewProduct(user, 1L);

        assertTrue(canReview);
    }

    @Test
    public void shouldGetAnEmptyListIfNoProductsWithThatCategoryFound() throws Exception {
        String categoryName = "Nonexistent Category";

        when(productService.getFilteredProducts(0, null, categoryName)).thenReturn(Page.empty());
        when(categoryService.getAllCategories()).thenReturn(List.of());

        mockMvc.perform(get("/products/show")
                        .param("page", "0")
                        .param("categoryName", categoryName))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("products", List.of()));

        verify(productService).getFilteredProducts(0, null, categoryName);
        verify(categoryService).getAllCategories();
    }

    @Test
    public void shouldReturnEmptyListIfNoProductsFoundWithTheGivenKeyword() throws Exception {
        String keyword = "Nonexistent Product";

        when(productService.getFilteredProducts(0, keyword, null)).thenReturn(Page.empty());
        when(categoryService.getAllCategories()).thenReturn(List.of());

        mockMvc.perform(get("/products/show")
                        .param("page", "0")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("products", List.of()));

        verify(productService).getFilteredProducts(0, keyword, null);
        verify(categoryService).getAllCategories();
    }
}
package com.onlinestore.art_supplies.reviews;

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

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateReview_Successfully() throws Exception {
        Review createdReview = new Review();
        createdReview.setReviewText("Great product!");

        when(reviewService.createReview(anyString(), anyLong(), anyString())).thenReturn(createdReview);

        mockMvc.perform(MockMvcRequestBuilders.post("/reviews")
                        .param("username", "user")
                        .content("""
                                {
                                    "reviewText": "Great product!",
                                    "productId": 1
                                }
                                """)
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.reviewText").value("Great product!"));
    }

    @Test
    void testCreateReview_UserNotFound() throws Exception {
        String username = "nonexistentUser ";
        when(reviewService.createReview(anyString(), anyLong(), eq(username)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with username: " + username));

        mockMvc.perform(MockMvcRequestBuilders.post("/reviews")
                        .param("username", username)
                        .content("""
                                {
                                    "reviewText": "Great product!",
                                    "productId": 1
                                }
                                """)
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("User not found with username: " + username));
    }

    @Test
    void testCreateReview_ProductNotFound() throws Exception {
        when(reviewService.createReview(anyString(), anyLong(), anyString()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: 1"));

        mockMvc.perform(MockMvcRequestBuilders.post("/reviews")
                        .param("username", "user")
                        .content("""
                                {
                                    "reviewText": "Great product!",
                                    "productId": 1
                                }
                                """)
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("Product not found with id: 1"));
    }

    @Test
    void testCreateReview_UserHasNotOrderedProduct() throws Exception {
        when(reviewService.createReview(anyString(), anyLong(), anyString()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has not ordered this product."));

        mockMvc.perform(MockMvcRequestBuilders.post("/reviews")
                        .param("username", "user")
                        .content("""
                                {
                                    "reviewText": "Great product!",
                                    "productId": 1
                                }
                                """)
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.status().reason("User has not ordered this product."));
    }

    @Test
    void testCreateReview_ValidationErrors() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/reviews")
                        .param("username", "user")
                        .content("""
                                {
                                    "productId": null,
                                    "reviewText": ""
                                
                                }
                                """)
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(containsString("Review text cannot be blank.")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Product ID cannot be null.")));
    }

    @Test
    void testDeleteReview_Successfully() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/reviews/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Review with id 1 was deleted successfully!"));
    }

    @Test
    void testDeleteReview_NotFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Review with 1 does not exist."))
                .when(reviewService).deleteReview(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/reviews/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("Review with 1 does not exist."));
    }

    @Test
    void testGetReviewsByProductId_Successfully() throws Exception {
        when(reviewService.getReviewsByProductId(1L)).thenReturn(List.of(new Review()));
        mockMvc.perform(MockMvcRequestBuilders.get("/reviews/product/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
    }

    @Test
    void testGetReviewsByProductId_NotFound() throws Exception {
        when(reviewService.getReviewsByProductId(1L)).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/reviews/product/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("No reviews found for product with id 1"));
    }

    @Test
    void testGetAllReviews_Successfully() throws Exception {
        when(reviewService.getAllReviews()).thenReturn(List.of(new Review(), new Review()));

        mockMvc.perform(MockMvcRequestBuilders.get("/reviews"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
    }
}
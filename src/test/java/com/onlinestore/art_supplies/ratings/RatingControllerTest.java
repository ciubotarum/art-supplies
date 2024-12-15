package com.onlinestore.art_supplies.ratings;

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

import static org.mockito.Mockito.*;

@WebMvcTest(RatingController.class)
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RatingService ratingService;

    private Rating rating1;
    private Rating rating2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rating1 = new Rating();
        rating2 = new Rating();
    }

    @Test
    void testCreateRating_Success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/ratings")
                        .contentType("application/json")
                        .content("""
                                {
                                  "ratingValue": 5, 
                                  "productId": 1,
                                  "username": "testUser"
                                 }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testCreateRating_UserNotFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with username: testUser"))
                .when(ratingService).createRating(anyInt(), anyLong(), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/ratings")
                        .contentType("application/json")
                        .content("""
                                {
                                  "ratingValue": 5, 
                                  "productId": 1,
                                  "username": "testUser"
                                 }
                                """))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found with username: testUser"));
    }

    @Test
    void testCreateRating_ProductNotFound() throws Exception {

        when(ratingService.createRating(anyInt(), anyLong(), anyString()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: 1"));

        mockMvc.perform(MockMvcRequestBuilders.post("/ratings")
                        .contentType("application/json")
                        .content("""
                                {
                                  "ratingValue": 5, 
                                  "productId": 1,
                                  "username": "testUser"
                                 }
                                """))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Product not found with id: 1"));
    }

    @Test
    void testCreateRating_UserHasNotOrderedProduct() throws Exception {

        when(ratingService.createRating(anyInt(), anyLong(), anyString()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has not ordered this product."));

        mockMvc.perform(MockMvcRequestBuilders.post("/ratings")
                        .contentType("application/json")
                        .content("""
                                {
                                  "ratingValue": 5, 
                                  "productId": 1,
                                  "username": "testUser"
                                 }
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User has not ordered this product."));
    }

    @Test
    void testCreateRating_ValidationError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/ratings")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testGetRatingsByProductId() throws Exception {
        when(ratingService.getRatingsByProductId(1L)).thenReturn(List.of(rating1, rating2));

        mockMvc.perform(MockMvcRequestBuilders.get("/ratings/product/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2));

    }

    @Test
    void testGetRatingsByProductId_InvalidProductId() throws Exception {
        when(ratingService.getRatingsByProductId(1L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No ratings found for product with id: 1"));

        mockMvc.perform(MockMvcRequestBuilders.get("/ratings/product/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("No ratings found for product with id: 1"));

    }

    @Test
    void testGetAllRatings() throws Exception {
        when(ratingService.getAllRatings()).thenReturn(List.of(rating1, rating2));

        mockMvc.perform(MockMvcRequestBuilders.get("/ratings/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2));
    }

    @Test
    void testGetAllRatings_NoRatingsFound() throws Exception {
        when(ratingService.getAllRatings()).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/ratings/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(0));
    }
}
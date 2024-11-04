package com.onlinestore.art_supplies.reviews;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "user_id")
    private Long userId;

    private int rating;

    @Column(name = "review_text")
    private String reviewText;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Review(String body) {
        this.reviewText = body;
    }
}

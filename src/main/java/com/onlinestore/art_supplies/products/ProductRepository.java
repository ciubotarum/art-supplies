package com.onlinestore.art_supplies.products;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p " +
            "JOIN p.category c " +
            "WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(String keyword);

    @Query("SELECT p FROM Product p  JOIN p.category c WHERE LOWER(c.categoryName) = LOWER(:categoryName)")
    List<Product> findByCategoryName(String categoryName);

    Page<Product> findByProductNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Product> findByCategory_CategoryNameIgnoreCase(String categoryName, Pageable pageable);

    Page<Product> findByProductNameContainingIgnoreCaseAndCategory_CategoryNameIgnoreCase(String keyword, String categoryName, Pageable pageable);
}

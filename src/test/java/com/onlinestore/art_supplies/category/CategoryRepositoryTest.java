package com.onlinestore.art_supplies.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("h2")
class CategoryRepositoryTest {

    CategoryRepository categoryRepository;

    @Autowired
    CategoryRepositoryTest(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @BeforeEach
    public void setUp() {
        categoryRepository.deleteAll();
        Category category = new Category();
        category.setCategoryName("Test Category");
        categoryRepository.save(category);
    }

    @Test
    public void shouldReturnCategoryIfFound() {
        Category foundCategory = categoryRepository.findByCategoryName("Test Category");
        assertNotNull(foundCategory);
        assertEquals("Test Category", foundCategory.getCategoryName());
        assertNotNull(foundCategory.getCategoryId());
    }

    @Test
    public void shouldReturnNullIfCategoryNotFound() {
        Category foundCategory = categoryRepository.findByCategoryName("Nonexistent Category");
        assertNull(foundCategory);
    }

    @Test
    @Transactional
    public void shouldDeleteCategoryIfExists() {
        categoryRepository.deleteByCategoryName("Test Category");
        Category foundCategory = categoryRepository.findByCategoryName("Test Category");
        assertNull(foundCategory);
    }

    @Test
    public void shouldNotDeleteCategoryIfNotExists() {
        categoryRepository.deleteByCategoryName("Nonexistent Category");
        Category foundCategory = categoryRepository.findByCategoryName("Test Category");
        assertNotNull(foundCategory);
    }

    @Test
    public void shouldReturnTrueIfCategoryExists() {
        boolean exists = categoryRepository.existsByCategoryName("Test Category");
        assertTrue(exists);
    }

    @Test
    public void shouldReturnFalseIfCategoryDoesNotExist() {
        boolean exists = categoryRepository.existsByCategoryName("Nonexistent Category");
        assertFalse(exists);
    }
}
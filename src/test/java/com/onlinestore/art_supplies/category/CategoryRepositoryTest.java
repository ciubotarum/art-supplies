package com.onlinestore.art_supplies.category;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("h2")
class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void shouldReturnCategoryIfFound() {
        Category category = new Category();
        category.setCategoryName("Test Category");
        categoryRepository.save(category);

        Category foundCategory = categoryRepository.findByCategoryName("Test Category");
        assertNotNull(foundCategory);
        assertEquals(category.getCategoryName(), foundCategory.getCategoryName());
        assertNotNull(foundCategory.getCategoryId());
    }


}
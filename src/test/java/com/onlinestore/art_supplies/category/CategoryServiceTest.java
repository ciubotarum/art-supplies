package com.onlinestore.art_supplies.category;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    CategoryService categoryService;

    @Test
    public void shouldReturnCategoryIfCreated() {
        Category category = new Category();

        category.setCategoryName("Test Category");

        when(categoryRepository.save(category)).thenReturn(category);

        Category createdCategory = categoryService.addCategory(category);

        assertEquals("Test Category", createdCategory.getCategoryName());
    }
}
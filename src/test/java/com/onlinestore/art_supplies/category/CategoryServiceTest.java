package com.onlinestore.art_supplies.category;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
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

    @Test
    public void shouldReturnCategoryAfterSuccessfullyUpdated() {
        Category category = new Category();
        category.setCategoryName("Test Category");
        String newCategoryName = "Updated Category";

        when(categoryRepository.findByCategoryName(category.getCategoryName())).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);

        Category updatedCategory = categoryService.updateCategoryByName(category.getCategoryName(), newCategoryName);
        assertEquals(newCategoryName, updatedCategory.getCategoryName());
    }

    @Test
    public void shouldReturnNotFoundExceptionWhenCategoryNotFoundForUpdating() {
        when(categoryRepository.findByCategoryName("Nonexistent Category")).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            categoryService.updateCategoryByName("Nonexistent Category", "Updated Category");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Category not found with name: Nonexistent Category", exception.getReason());

    }

    @Test
    public void shouldDeleteSuccessfullyCategory() {
        String categoryName = "Test Category";

        when(categoryRepository.existsByCategoryName(categoryName)).thenReturn(true);
        categoryService.deleteCategoryByName(categoryName);
        verify(categoryRepository).deleteByCategoryName(categoryName);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenDeletingNonexistentCategory() {
        String categoryName = "Nonexistent Category";
        when(categoryRepository.existsByCategoryName(categoryName)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            categoryService.deleteCategoryByName(categoryName);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Category not found with name: " + categoryName, exception.getReason());
    }

    @Test
    public void shouldReturnAllCategories() {
        Category category1 = new Category();
        category1.setCategoryName("Category 1");
        Category category2 = new Category();
        category2.setCategoryName("Category 2");

        when(categoryRepository.findAll()).thenReturn(List.of(category1, category2));

        List<Category> categories = categoryService.getAllCategories();
        assertEquals(2, categories.size());
        assertEquals("Category 1", categories.get(0).getCategoryName());
        assertEquals("Category 2", categories.get(1).getCategoryName());
    }

    @Test
    public void shouldReturnAnEmptyListIfNoCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of());

        List<Category> categories = categoryService.getAllCategories();
        assertEquals(0, categories.size());
    }

    @Test
    public void shouldReturnCategoryIfFoundById() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setCategoryName("Test Category");

        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.of(category));

        Category foundCategory = categoryService.getCategoryById(categoryId);
        assertEquals("Test Category", foundCategory.getCategoryName());
    }

    @Test
    public void shouldReturnNotFoundExceptionIfCategoryNotFoundById() {
        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            categoryService.getCategoryById(categoryId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Category not found with id " + categoryId, exception.getReason());
    }
}
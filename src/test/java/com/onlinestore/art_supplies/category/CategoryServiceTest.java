package com.onlinestore.art_supplies.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCategory() {
        Category category = new Category();
        category.setCategoryName("Painting");

        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.addCategory(category);

        assertNotNull(result);
        assertEquals("Painting", result.getCategoryName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void testUpdateCategoryByName_Success() {
        Category existingCategory = new Category();
        existingCategory.setCategoryName("Painting");

        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("Drawing");

        when(categoryRepository.findByCategoryName("Painting")).thenReturn(existingCategory);
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

        Category result = categoryService.updateCategoryByName("Painting", updatedCategory);

        assertNotNull(result);
        assertEquals("Drawing", result.getCategoryName());
        verify(categoryRepository, times(1)).findByCategoryName("Painting");
        verify(categoryRepository, times(1)).save(existingCategory);
    }

    @Test
    void testUpdateCategoryByName_NotFound() {
        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("Drawing");

        when(categoryRepository.findByCategoryName("Painting")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategoryByName("Painting", updatedCategory);
        });

        assertEquals("404 NOT_FOUND \"Category not found with name: Painting\"", exception.getMessage());
        verify(categoryRepository, times(1)).findByCategoryName("Painting");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testDeleteCategory() {
        Long categoryId = 1L;

        doNothing().when(categoryRepository).deleteById(categoryId);

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    void testCategoryExistsById() {
        Long categoryId = 1L;

        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        boolean exists = categoryService.categoryExistsById(categoryId);

        assertTrue(exists);
        verify(categoryRepository, times(1)).existsById(categoryId);
    }

    @Test
    void testGetAllCategories() {
        Category category1 = new Category();
        category1.setCategoryName("Painting");

        Category category2 = new Category();
        category2.setCategoryName("Drawing");

        List<Category> categories = Arrays.asList(category1, category2);

        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testGetCategoryById_Success() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setCategoryName("Painting");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(categoryId);

        assertNotNull(result);
        assertEquals("Painting", result.getCategoryName());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testGetCategoryById_NotFound() {
        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            categoryService.getCategoryById(categoryId);
        });

        assertEquals("404 NOT_FOUND \"Category not found with id " + categoryId + "\"", exception.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
    }
}
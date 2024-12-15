package com.onlinestore.art_supplies.category;

import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CategoryController categoryController;

    private User adminUser;
    private User nonAdminUser;
    private Category category;
    private List<Category> categories;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        adminUser = new User();
        adminUser.setIsAdmin(true);

        nonAdminUser = new User();
        nonAdminUser.setIsAdmin(false);

        category = new Category();
        category.setCategoryName("Painting");

        Category category2 = new Category();
        category2.setCategoryName("Drawing");

        categories = Arrays.asList(category, category2);
    }

    @Test
    void testAddCategory_Success() {

        when(userService.getUserById(1L)).thenReturn(Optional.of(adminUser));
        when(categoryService.addCategory(category)).thenReturn(category);

        ResponseEntity<?> response = categoryController.addCategory(category, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(category, response.getBody());
        verify(userService, times(1)).getUserById(1L);
        verify(categoryService, times(1)).addCategory(category);
    }

    @Test
    void testAddCategory_Forbidden() {

        when(userService.getUserById(1L)).thenReturn(Optional.of(nonAdminUser));

        ResponseEntity<?> response = categoryController.addCategory(category, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Only the admin can add the categories.", response.getBody());
        verify(userService, times(1)).getUserById(1L);
        verify(categoryService, never()).addCategory(any(Category.class));
    }

    @Test
    void testGetAllCategories_Success() {

        when(userService.getUserById(1L)).thenReturn(Optional.of(adminUser));
        when(categoryService.getAllCategories()).thenReturn(categories);

        ResponseEntity<List<Category>> response = categoryController.getAllCategories(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categories, response.getBody());
        verify(userService, times(1)).getUserById(1L);
        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    void testGetAllCategories_Forbidden() {

        when(userService.getUserById(1L)).thenReturn(Optional.of(nonAdminUser));

        ResponseEntity<List<Category>> response = categoryController.getAllCategories(1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(userService, times(1)).getUserById(1L);
        verify(categoryService, never()).getAllCategories();
    }

    @Test
    void testUpdateCategoryByName_Success() {

        when(userService.getUserById(1L)).thenReturn(Optional.of(adminUser));
        when(categoryService.updateCategoryByName("Painting", category)).thenReturn(category);

        ResponseEntity<?> response = categoryController.updateCategoryByName(category, "Painting", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(category, response.getBody());
        verify(userService, times(1)).getUserById(1L);
        verify(categoryService, times(1)).updateCategoryByName("Painting", category);
    }

    @Test
    void testUpdateCategoryByName_Forbidden() {

        when(userService.getUserById(1L)).thenReturn(Optional.of(nonAdminUser));

        ResponseEntity<?> response = categoryController.updateCategoryByName(category, "Painting", 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(userService, times(1)).getUserById(1L);
        verify(categoryService, never()).updateCategoryByName(anyString(), any(Category.class));
    }

    @Test
    void testDeleteCategory_Success() {

        when(userService.getUserById(1L)).thenReturn(Optional.of(adminUser));
        when(categoryService.categoryExistsById(1L)).thenReturn(true);
        doNothing().when(categoryService).deleteCategory(1L);

        ResponseEntity<?> response = categoryController.deleteCategory(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).getUserById(1L);
        verify(categoryService, times(1)).categoryExistsById(1L);
        verify(categoryService, times(1)).deleteCategory(1L);
    }

    @Test
    void testDeleteCategory_NotFound() {

        when(userService.getUserById(1L)).thenReturn(Optional.of(adminUser));
        when(categoryService.categoryExistsById(1L)).thenReturn(false);

        ResponseEntity<?> response = categoryController.deleteCategory(1L, 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("There is no such category with the given id.", response.getBody());
        verify(userService, times(1)).getUserById(1L);
        verify(categoryService, times(1)).categoryExistsById(1L);
        verify(categoryService, never()).deleteCategory(1L);
    }

    @Test
    void testDeleteCategory_Forbidden() {

        when(userService.getUserById(1L)).thenReturn(Optional.of(nonAdminUser));

        ResponseEntity<?> response = categoryController.deleteCategory(1L, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(userService, times(1)).getUserById(1L);
        verify(categoryService, never()).categoryExistsById(anyLong());
        verify(categoryService, never()).deleteCategory(anyLong());
    }
}
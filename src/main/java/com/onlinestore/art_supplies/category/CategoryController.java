package com.onlinestore.art_supplies.category;

import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;
    private final UserService userService;

    @Autowired
    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Add a new category",
            description = "Add a new category to the database",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category added"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    public ResponseEntity<?> addCategory(@RequestBody Category category, @RequestParam Long adminId) {
        User adminUser = userService.getUserById(adminId).orElse(null);
        if (adminUser != null && Boolean.TRUE.equals(adminUser.getIsAdmin())) {
            return ResponseEntity.ok(categoryService.addCategory(category));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only the admin can add the categories.");
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Get all categories",
            description = "Get all categories from the database",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Categories found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    public ResponseEntity<List<Category>> getAllCategories(@RequestParam Long adminId) {
        User adminUser = userService.getUserById(adminId).orElse(null);
        if (adminUser != null && Boolean.TRUE.equals(adminUser.getIsAdmin())) {
            return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{categoryName}")
    @Operation(summary = "Update category by name",
            description = "Update category by name in the database",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category updated"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    public ResponseEntity<?> updateCategoryByName(@RequestBody Category category, @PathVariable String categoryName, @RequestParam Long adminId) {
        User adminUser = userService.getUserById(adminId).orElse(null);
        if (adminUser != null && Boolean.TRUE.equals(adminUser.getIsAdmin())) {
            return ResponseEntity.ok(categoryService.updateCategoryByName(categoryName, category));
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping
    @Operation(summary = "Delete category",
            description = "Delete category by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category deleted"),
                    @ApiResponse(responseCode = "404", description = "Category not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    public ResponseEntity<?> deleteCategory(@RequestParam Long adminId, @RequestParam Long categoryId) {
        User adminUser = userService.getUserById(adminId).orElse(null);

        if (adminUser != null && Boolean.TRUE.equals(adminUser.getIsAdmin())) {
            if (categoryId != null && categoryService.categoryExistsById(categoryId)) { // Check if category exists
                categoryService.deleteCategory(categoryId);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no such category with the given id.");
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

}

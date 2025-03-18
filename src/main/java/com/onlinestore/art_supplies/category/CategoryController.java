package com.onlinestore.art_supplies.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Tag(name = "Category Controller", description = "Operations related to categories")
@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    @Operation(summary = "Add a new category",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category added"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })

    public ResponseEntity<?> addCategory(@Valid @RequestBody Category category) {
            return ResponseEntity.ok(categoryService.addCategory(category));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all categories",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Categories found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    public ResponseEntity<List<Category>> getAllCategories() {
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{categoryName}")
    @Operation(summary = "Update category by name",
            parameters = {
                    @Parameter(name = "categoryName", description = "The name of the category to update", required = true, example = "Water Colors")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category updated"),
                    @ApiResponse(responseCode = "404", description = "Category not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    public ResponseEntity<?> updateCategoryByName(@RequestBody Category category, @PathVariable String categoryName) {
        try {
            return ResponseEntity.ok(categoryService.updateCategoryByName(categoryName, category));
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{categoryName}")
    @Operation(summary = "Delete category by name",
            parameters = {
                    @Parameter(name = "categoryName", description = "The name of the category to delete", required = true, example = "Water Colors")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category deleted"),
                    @ApiResponse(responseCode = "404", description = "Category not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden, the user is not an admin")
            })
    public ResponseEntity<?> deleteCategory(@PathVariable String categoryName) {
        try {
            categoryService.deleteCategoryByName(categoryName);
            return ResponseEntity.ok("Category deleted successfully");
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        }
    }
}

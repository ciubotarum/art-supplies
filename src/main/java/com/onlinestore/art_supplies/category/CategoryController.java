package com.onlinestore.art_supplies.category;

import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserService;
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
    public ResponseEntity<?> addCategory(@RequestBody Category category, @RequestParam Long adminId) {
        User adminUser = userService.getUserById(adminId).orElse(null);
        if (adminUser != null && Boolean.TRUE.equals(adminUser.getIsAdmin())) {
            return ResponseEntity.ok(categoryService.addCategory(category));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only the admin can add the categories.");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategories(@RequestParam Long adminId) {
        User adminUser = userService.getUserById(adminId).orElse(null);
        if (adminUser != null && Boolean.TRUE.equals(adminUser.getIsAdmin())) {
            return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{categoryName}")
    public ResponseEntity<?> updateCategoryByName(@RequestBody Category category, @PathVariable String categoryName, @RequestParam Long adminId) {
        User adminUser = userService.getUserById(adminId).orElse(null);
        if (adminUser != null && Boolean.TRUE.equals(adminUser.getIsAdmin())) {
            return ResponseEntity.ok(categoryService.updateCategoryByName(categoryName, category));
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping
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

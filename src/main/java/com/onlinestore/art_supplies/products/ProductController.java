package com.onlinestore.art_supplies.products;

import com.onlinestore.art_supplies.category.Category;
import com.onlinestore.art_supplies.category.CategoryService;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final UserService userService;
    private final CategoryService categoryService;

    @Autowired
    public ProductController(ProductService productService, UserService userService, CategoryService categoryService) {
        this.productService = productService;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> addProduct(
            @RequestBody Product product,
            @RequestParam Long adminId,
            @RequestParam Long categoryId) {

        if (!product.getImage().startsWith("http")) {
            return new ResponseEntity<>("Not a valid URL", HttpStatus.BAD_REQUEST);
        }

        User adminUser = userService.getUserById(adminId).orElse(null);

        if (adminUser != null && Boolean.TRUE.equals(adminUser.getIsAdmin())) {
            // Load the category from the database
            Category category = categoryService.getCategoryById(categoryId);
            if (category == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Category not found");
            }

            product.setCategory(category); // Assign the loaded category
            Product savedProduct = productService.addProduct(product);
            return ResponseEntity.ok(savedProduct);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Only admins can add a product.");
        }
    }


    @DeleteMapping
    public ResponseEntity<?> deleteProduct(@RequestParam Long productId, @RequestParam Long adminId) {
        User adminUser = userService.getUserById(adminId).orElse(null);
        // Check if the user is an admin
        if (adminUser  == null || !Boolean.TRUE.equals(adminUser .getIsAdmin())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admin can delete a product.");
        }
        // Check if the product exists
        if (!productService.productExistsById(productId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product found with the given ID.");
        }
        // Delete the product
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully!");
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestParam Long adminId, @RequestBody Product updatedProduct) {
        User adminUser = userService.getUserById(adminId).orElse(null);
        if (adminUser  == null || !Boolean.TRUE.equals(adminUser .getIsAdmin())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admin can delete a product.");
        }
        Product updated = productService.updateProduct(productId, updatedProduct);
        return ResponseEntity.ok(updated);
    }
}

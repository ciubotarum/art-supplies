package com.onlinestore.art_supplies.products;

import com.onlinestore.art_supplies.category.Category;
import com.onlinestore.art_supplies.category.CategoryService;
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
    @Operation(summary = "Get all products",
            description = "Get all products from the database",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Products found"),
                    @ApiResponse(responseCode = "404", description = "No products found")
            })
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/product/{id}")
    @Operation(summary = "Get product by ID",
            description = "Get a product by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product found"),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            })
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @Operation(summary = "Add a new product",
            description = "Add a new product to the database",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product added"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            })
    public ResponseEntity<?> addProduct(
            @RequestBody Product product,
            @RequestParam Long adminId,
            @RequestParam Long categoryId) {

        if (!product.getImage().startsWith("http")) {
            return new ResponseEntity<>("Not a valid URL", HttpStatus.BAD_REQUEST);
        }

        User adminUser = userService.getUserById(adminId).orElse(null);

        if (adminUser != null && Boolean.TRUE.equals(adminUser.getIsAdmin())) {
            Category category = categoryService.getCategoryById(categoryId);
            if (category == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Category not found");
            }

            product.setCategory(category);
            Product savedProduct = productService.addProduct(product);
            return ResponseEntity.ok(savedProduct);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Only admins can add a product.");
        }
    }


    @DeleteMapping
    @Operation(summary = "Delete product",
            description = "Delete a product by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product deleted"),
                    @ApiResponse(responseCode = "404", description = "Product not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    public ResponseEntity<?> deleteProduct(@RequestParam Long productId, @RequestParam Long adminId) {
        User adminUser = userService.getUserById(adminId).orElse(null);
        if (adminUser  == null || !Boolean.TRUE.equals(adminUser .getIsAdmin())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admin can delete a product.");
        }
        if (!productService.productExistsById(productId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product found with the given ID.");
        }
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully!");
    }

    @GetMapping("/search")
    @Operation(summary = "Search products",
            description = "Search products by keyword",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Products found"),
                    @ApiResponse(responseCode = "404", description = "No products found")
            })
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update product",
            description = "Update a product by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product updated"),
                    @ApiResponse(responseCode = "404", description = "Product not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestParam Long adminId, @RequestBody Product updatedProduct) {
        User adminUser = userService.getUserById(adminId).orElse(null);
        if (adminUser  == null || !Boolean.TRUE.equals(adminUser .getIsAdmin())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admin can delete a product.");
        }
        Product updated = productService.updateProduct(productId, updatedProduct);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter products by category",
            description = "Filter products by category name",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Products found"),
                    @ApiResponse(responseCode = "404", description = "No products found")
            })
    public ResponseEntity<List<Product>> filterProductsByType(@RequestParam String categoryName) {
        List<Product> products = productService.getProductsByCategoryName(categoryName);
        return ResponseEntity.ok(products);
    }
}

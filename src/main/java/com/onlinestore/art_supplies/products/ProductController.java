package com.onlinestore.art_supplies.products;

import com.onlinestore.art_supplies.category.Category;
import com.onlinestore.art_supplies.category.CategoryService;
import com.onlinestore.art_supplies.users.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Tag(name = "Product Controller", description = "Operations related to products")
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final UserService userService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, UserService userService, CategoryService categoryService) {
        this.productService = productService;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @GetMapping("/all")
    @Operation(summary = "Retrieve all products",
            description = "Fetch a list of all products available in the database. Each product includes details such " +
                    "as productId, productName, description, price, quantity, category, and image URL",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of products")
            })
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/product/{id}")
    @Operation(summary = "Retrieve a product by its ID",
            description = "Fetch detailed information about a specific product using its unique ID. Returns the product" +
                    " details if found, or an error message if the product does not exist.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "The unique identifier of the product to retrieve",
                            required = true,
                            example = "7"
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product found"),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            })
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @Operation(summary = "Add a new product",
            description = "Add a new product to the database",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Put productName, description, price, quantity, image"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product added"),
                    @ApiResponse(responseCode = "404", description = "Category not found, or user not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden for non-admin users"),

            })
    public ResponseEntity<?> addProduct(
            @RequestBody @Valid Product product,
            @RequestParam Long adminId,
            @RequestParam Long categoryId) {

        userService.checkAdminAndLoggedIn(adminId);

        Category category = categoryService.getCategoryById(categoryId);
        if (category == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found");
        }

        product.setCategory(category);
        Product savedProduct = productService.addProduct(product);
        return ResponseEntity.ok(savedProduct);
    }


    @DeleteMapping
    @Operation(summary = "Delete product",
            description = "Delete a product by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product deleted"),
                    @ApiResponse(responseCode = "404", description = "Product not found or user not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden for non-admin users")
            })
    public ResponseEntity<?> deleteProduct(@RequestParam Long productId, @RequestParam Long adminId) {
        userService.checkAdminAndLoggedIn(adminId);

        if (!productService.productExistsById(productId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product found with the given ID.");
        }
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully!");
    }

    @GetMapping("/search")
    @Operation(summary = "Search for products",
            description = "Allows users to search for products by providing a keyword. The search matches product " +
                    "names or descriptions containing the given keyword.",
            parameters = {
            @Parameter(name = "keyword", description = "The keyword to search for. The search is case-insensitive.",
                    required = true, example = "brush"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Search results found"),
            })
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update product",
            description = "Update a product by ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Put productName, description, price, quantity, image, categoryId"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product updated"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "404", description = "Product not found or user not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden for non-admin users")
            })
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestParam Long adminId, @RequestBody @Valid Product updatedProduct) {
        userService.checkAdminAndLoggedIn(adminId);

        if (!productService.productExistsById(productId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product found with the given ID.");
        }
        Product updated = productService.updateProduct(productId, updatedProduct);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter products by category",
            description = "Filter products by category name",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Products found"),
                    @ApiResponse(responseCode = "404", description = "No such category found")
            })
    public ResponseEntity<?> filterProductsByCategory(
            @RequestParam @Parameter(description = "Name of the category to filter products by", example = "brushes") String categoryName) {
        List<Product> products = productService.getProductsByCategoryName(categoryName);
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such category");
        }
        return ResponseEntity.ok(products);
    }
}

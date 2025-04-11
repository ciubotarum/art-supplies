package com.onlinestore.art_supplies.view;

import com.onlinestore.art_supplies.category.Category;
import com.onlinestore.art_supplies.category.CategoryService;
import com.onlinestore.art_supplies.products.Product;
import com.onlinestore.art_supplies.products.ProductService;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
public class AdminController {
    private  final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;

    public AdminController(ProductService productService, CategoryService categoryService, UserService userService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String adminPage(Model model, HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        if (user == null || Boolean.FALSE.equals(user.getIsAdmin())) {
            return "redirect:/login";
        }
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin";
    }

    @PostMapping("/admin/add-category")
    public String addCategory(@RequestParam String categoryName) {
        Category category = new Category();
        category.setCategoryName(categoryName);
        categoryService.addCategory(category);

        return "redirect:/admin";
    }

    @PostMapping("/admin/add-product")
    public String addProduct(@RequestParam String productName,
                             @RequestParam String description,
                             @RequestParam BigDecimal price,
                             @RequestParam int quantity,
                             @RequestParam Long categoryId,
                             @RequestParam String image) {
        Product product = new Product();
        product.setProductName(productName);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setImage(image);
        product.setCategory(categoryService.getCategoryById(categoryId));
        productService.addProduct(product);
        return "redirect:/admin";
    }
}

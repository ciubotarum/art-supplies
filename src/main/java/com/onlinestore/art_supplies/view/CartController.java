package com.onlinestore.art_supplies.view;

import com.onlinestore.art_supplies.order.cart.CartService;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public String viewCart(Model model, HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        model.addAttribute("cartItems", cartService.getCartItems(user));
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        cartService.addProductToCart(user, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long productId,
                                 @RequestParam int quantity,
                                 HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        cartService.updateCartItemQuantity(user, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeItem(@RequestParam Long productId,
                             HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        cartService.removeProductFromCart(user, productId);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        cartService.clearCart(user);
        return "redirect:/cart";
    }
}

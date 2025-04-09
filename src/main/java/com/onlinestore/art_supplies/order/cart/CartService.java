package com.onlinestore.art_supplies.order.cart;

import com.onlinestore.art_supplies.products.Product;
import com.onlinestore.art_supplies.products.ProductRepository;
import com.onlinestore.art_supplies.users.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public Cart getOrCreateCartForUser(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    public void addProductToCart(User user, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Cart cart = getOrCreateCartForUser(user);

        Optional<CartItem> optionalCartItem = cartItemRepository.findByCartAndProduct(cart, product);
        CartItem cartItem;

        if (optionalCartItem.isPresent()) {
            cartItem = optionalCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }

        cartItemRepository.save(cartItem);
    }

    public List<CartItem> getCartItems(User user) {
        Cart cart = getOrCreateCartForUser(user);
        return cart.getCartItems();
    }

    public void updateCartItemQuantity(User user, Long productId, int quantity) {
        Cart cart = getOrCreateCartForUser(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new IllegalArgumentException("Item not in cart"));

        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    public void removeProductFromCart(User user, Long productId) {
        Cart cart = getOrCreateCartForUser(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new IllegalArgumentException("Item not in cart"));

        cartItemRepository.delete(item);
    }

    public void clearCart(User user) {
        Cart cart = getOrCreateCartForUser(user);
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }
}

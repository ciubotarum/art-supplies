package com.onlinestore.art_supplies.products;

import com.onlinestore.art_supplies.category.Category;
import com.onlinestore.art_supplies.category.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product addProduct(Product product) {
        if (product.getCategory() != null && product.getCategory().getCategoryId() != null) {
            Category existingCategory = categoryRepository.findById(product.getCategory().getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
            product.setCategory(existingCategory);
        }
        return productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id " + productId);
        }
        productRepository.deleteById(productId);
    }

    public List<Product> searchProducts(String keyword) {
        return (keyword == null || keyword.trim().isEmpty()) ? List.of() : productRepository.searchProducts(keyword);
    }

    public boolean productExistsById(Long productId) {
        return productRepository.existsById(productId);
    }

    public Product updateProduct(Long productId, Product updatedProduct) {
        Optional<Product> existingProductOptional = productRepository.findById(productId);

        if (existingProductOptional.isPresent()) {
            Product existingProduct = existingProductOptional.get();

            existingProduct.setProductName(updatedProduct.getProductName());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setQuantity(updatedProduct.getQuantity());
            existingProduct.setImage(updatedProduct.getImage());
            existingProduct.setCategory(updatedProduct.getCategory());

            return productRepository.save(existingProduct);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id " + productId);
        }
    }

    public List<Product> getProductsByCategoryName(String categoryName) {
        return productRepository.findByCategoryName(categoryName);
    }

    public Page<Product> getFilteredProducts(int page, String keyword, String categoryName) {
        Pageable pageable = PageRequest.of(page, 6); // 6 products per page
        if ((keyword == null || keyword.isEmpty()) && (categoryName == null || categoryName.isEmpty())) {
            return productRepository.findAll(pageable);
        } else if (keyword != null && !keyword.isEmpty() && (categoryName == null || categoryName.isEmpty())) {
            return productRepository.findByProductNameContainingIgnoreCase(keyword, pageable);
        } else if ((keyword == null || keyword.isEmpty()) && categoryName != null && !categoryName.isEmpty()) {
            return productRepository.findByCategory_CategoryNameIgnoreCase(categoryName, pageable);
        } else {
            return productRepository.findByProductNameContainingIgnoreCaseAndCategory_CategoryNameIgnoreCase(keyword, categoryName, pageable);
        }
    }
}

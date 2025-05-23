package com.onlinestore.art_supplies.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category addCategory(Category category) {
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category already exists with name: " + category.getCategoryName());
        }
        return categoryRepository.save(category);
    }

    public Category updateCategoryByName(String categoryName, String newCategoryName) {
        Category existingCategory = categoryRepository.findByCategoryName(categoryName);
        if (existingCategory == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with name: " + categoryName);
        }
        if (categoryRepository.existsByCategoryName(newCategoryName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category already exists with name: " + newCategoryName);
        }
        existingCategory.setCategoryName(newCategoryName);
        return categoryRepository.save(existingCategory);
    }

    @Transactional
    public void deleteCategoryByName(String categoryName) {
        if (categoryRepository.existsByCategoryName(categoryName)) {
            categoryRepository.deleteByCategoryName(categoryName);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with name: " + categoryName);
        }
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id " + categoryId));
    }
}

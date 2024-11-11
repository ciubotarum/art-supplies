package com.onlinestore.art_supplies.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategoryByName(String categoryName, Category updatedCategory) {
        Category existingCategory = categoryRepository.findByCategoryName(categoryName);
        if (existingCategory != null) {
            existingCategory.setCategoryName(updatedCategory.getCategoryName());
            return categoryRepository.save(existingCategory);
        } else {
            throw new RuntimeException("Category not found with name: " + categoryName);
        }
    }

    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
    public boolean categoryExistsById(Long categoryId) {
        return categoryRepository.existsById(categoryId);
    }
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}

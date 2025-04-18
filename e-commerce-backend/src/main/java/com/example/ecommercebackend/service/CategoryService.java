package com.example.ecommercebackend.service;

import com.example.ecommercebackend.dto.CategoryDTO;
import com.example.ecommercebackend.dto.CategoryResponse;

public interface CategoryService {
    CategoryResponse fetchCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);

    void deleteCategory(Long id);
}

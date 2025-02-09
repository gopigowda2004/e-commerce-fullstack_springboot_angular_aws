package com.example.ecommercebackend.service;

import com.example.ecommercebackend.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> fetchCategories();

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);

    void deleteCategory(Long id);
}

package com.nguyenhuuquang.doanketthucmon.cafe.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.CategoryRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.CategoryResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.Category;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.CategoryRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private CategoryResponse convertToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        BeanUtils.copyProperties(category, response);
        // Không cần chuyển sang String nữa
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        return response;
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Category already exists with name: " + request.getName());
        }

        Category category = new Category();
        BeanUtils.copyProperties(request, category);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        return convertToResponse(categoryRepository.save(category));
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return convertToResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        BeanUtils.copyProperties(request, category, "id", "createdAt");

        category.setUpdatedAt(LocalDateTime.now());
        return convertToResponse(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}

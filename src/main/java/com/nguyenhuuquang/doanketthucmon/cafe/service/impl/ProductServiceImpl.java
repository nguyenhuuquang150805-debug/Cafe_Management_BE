package com.nguyenhuuquang.doanketthucmon.cafe.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.CategoryResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.ProductRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.ProductResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.Category;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.Product;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.CategoryRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.ProductRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(category)
                .imageUrl(request.getImageUrl())
                .stockQuantity(request.getStockQuantity())
                .isActive(request.getIsActive())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productRepository.save(product);
        return mapToResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        product.setImageUrl(request.getImageUrl());
        product.setStockQuantity(request.getStockQuantity());
        product.setIsActive(request.getIsActive());
        product.setUpdatedAt(LocalDateTime.now());

        productRepository.save(product);
        return mapToResponse(product);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private ProductResponse mapToResponse(Product product) {
        Category category = product.getCategory();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(category.getId());
        categoryResponse.setName(category.getName());
        categoryResponse.setDescription(category.getDescription());
        categoryResponse.setImageUrl(category.getImageUrl());
        categoryResponse.setCreatedAt(category.getCreatedAt());
        categoryResponse.setUpdatedAt(category.getUpdatedAt());

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(categoryResponse) // gắn object CategoryResponse
                .imageUrl(product.getImageUrl())
                .stockQuantity(product.getStockQuantity())
                .isActive(product.getIsActive())
                .build();
    }

}

package com.nguyenhuuquang.doanketthucmon.cafe.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.CategoryResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.ProductResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.PromotionRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.PromotionResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.Category;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.Product;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.Promotion;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.ProductRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.PromotionRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.service.PromotionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;

    @Override
    public PromotionResponse createPromotion(PromotionRequest request) {
        Promotion promotion = new Promotion();
        promotion.setName(request.getName());
        promotion.setDiscountPercentage(request.getDiscountPercentage());
        promotion.setDiscountAmount(request.getDiscountAmount());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setIsActive(request.getIsActive());
        promotion.setCreatedAt(LocalDateTime.now());
        promotion.setUpdatedAt(LocalDateTime.now());

        // Thêm products đồng bộ 2 chiều
        if (request.getProductIds() != null && !request.getProductIds().isEmpty()) {
            productRepository.findAllById(request.getProductIds())
                    .forEach(promotion::addProduct);
        }

        promotionRepository.saveAndFlush(promotion);
        return mapToResponse(promotion);
    }

    @Override
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAllWithProducts().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PromotionResponse getPromotionById(Long id) {
        Promotion promotion = promotionRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
        return mapToResponse(promotion);
    }

    @Override
    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        Promotion promotion = promotionRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));

        promotion.setName(request.getName());
        promotion.setDiscountPercentage(request.getDiscountPercentage());
        promotion.setDiscountAmount(request.getDiscountAmount());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setIsActive(request.getIsActive());
        promotion.setUpdatedAt(LocalDateTime.now());

        // Xóa mapping cũ
        promotion.getProducts().forEach(p -> p.getPromotions().remove(promotion));
        promotion.getProducts().clear();

        if (request.getProductIds() != null && !request.getProductIds().isEmpty()) {
            productRepository.findAllById(request.getProductIds())
                    .forEach(promotion::addProduct);
        }

        promotionRepository.saveAndFlush(promotion);
        return mapToResponse(promotion);
    }

    @Override
    public void deletePromotion(Long id) {
        Promotion promotion = promotionRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));

        // Xóa mapping trước để tránh lỗi 403
        for (Product p : promotion.getProducts()) {
            p.getPromotions().remove(promotion);
        }
        promotion.getProducts().clear();
        promotionRepository.saveAndFlush(promotion);

        promotionRepository.delete(promotion);
    }

    private PromotionResponse mapToResponse(Promotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .discountPercentage(promotion.getDiscountPercentage())
                .discountAmount(promotion.getDiscountAmount())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .isActive(promotion.getIsActive())
                .createdAt(promotion.getCreatedAt())
                .updatedAt(promotion.getUpdatedAt())
                .products(promotion.getProducts().stream()
                        .map(p -> ProductResponse.builder()
                                .id(p.getId())
                                .name(p.getName())
                                .description(p.getDescription())
                                .price(p.getPrice())
                                .category(mapCategoryToResponse(p.getCategory()))
                                .imageUrl(p.getImageUrl())
                                .stockQuantity(p.getStockQuantity())
                                .isActive(p.getIsActive())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }

    private CategoryResponse mapCategoryToResponse(Category category) {
        if (category == null)
            return null;
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setImageUrl(category.getImageUrl());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        return response;
    }
}

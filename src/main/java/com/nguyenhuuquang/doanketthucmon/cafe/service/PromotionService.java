package com.nguyenhuuquang.doanketthucmon.cafe.service;

import java.util.List;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.PromotionRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.PromotionResponse;

public interface PromotionService {
    PromotionResponse createPromotion(PromotionRequest request);

    List<PromotionResponse> getAllPromotions();

    PromotionResponse getPromotionById(Long id);

    PromotionResponse updatePromotion(Long id, PromotionRequest request);

    void deletePromotion(Long id);
}

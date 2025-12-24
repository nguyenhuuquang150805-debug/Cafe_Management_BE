package com.nguyenhuuquang.doanketthucmon.cafe.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromotionResponse {
    private Long id;
    private String name;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<ProductResponse> products;
}

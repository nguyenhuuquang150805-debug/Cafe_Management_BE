package com.nguyenhuuquang.doanketthucmon.cafe.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import lombok.Data;

@Data
public class PromotionRequest {
    private String name;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive = true;
    private Set<Long> productIds; // danh sách product id được áp dụng
}

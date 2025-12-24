package com.nguyenhuuquang.doanketthucmon.cafe.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private TableResponse table;
    private UserResponse employee;
    private String status;
    private BigDecimal totalAmount;
    private PromotionResponse promotion;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> items;
}

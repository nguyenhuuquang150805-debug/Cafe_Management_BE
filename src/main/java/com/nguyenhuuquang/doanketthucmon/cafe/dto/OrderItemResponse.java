package com.nguyenhuuquang.doanketthucmon.cafe.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponse {
    private Long id;
    private ProductResponse product;
    private OrderResponse order;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

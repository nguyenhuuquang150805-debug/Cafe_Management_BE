package com.nguyenhuuquang.doanketthucmon.cafe.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

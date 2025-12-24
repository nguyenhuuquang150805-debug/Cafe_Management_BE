package com.nguyenhuuquang.doanketthucmon.cafe.dto;

import java.math.BigDecimal;
import java.util.List;

import io.micrometer.common.lang.Nullable;
import lombok.Data;

@Data
public class OrderRequest {
    @Nullable
    private Long tableId;
    private Long employeeId;
    private String status;
    private BigDecimal totalAmount;
    private Long promotionId;
    private String notes;
    private List<OrderItemRequest> items;
}

package com.nguyenhuuquang.doanketthucmon.cafe.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private String imageUrl;
    private Integer stockQuantity;
    private Boolean isActive;
}

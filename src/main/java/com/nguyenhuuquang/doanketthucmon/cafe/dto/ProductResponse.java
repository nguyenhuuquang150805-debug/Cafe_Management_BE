package com.nguyenhuuquang.doanketthucmon.cafe.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private CategoryResponse category;
    private String imageUrl;
    private Integer stockQuantity;
    private Boolean isActive;
}

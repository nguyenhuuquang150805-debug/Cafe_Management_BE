package com.nguyenhuuquang.doanketthucmon.cafe.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.nguyenhuuquang.doanketthucmon.cafe.entity.enums.PaymentMethod;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.enums.PaymentStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BillRequest {
    private Long orderId;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime issuedAt;
    private String notes;
}

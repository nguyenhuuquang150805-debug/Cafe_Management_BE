package com.nguyenhuuquang.doanketthucmon.cafe.dto;

import java.time.LocalDateTime;

import com.nguyenhuuquang.doanketthucmon.cafe.entity.enums.Status;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TableResponse {
    private Long id;
    private Integer number;
    private Integer capacity;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

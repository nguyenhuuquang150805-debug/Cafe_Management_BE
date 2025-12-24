package com.nguyenhuuquang.doanketthucmon.cafe.dto;

import com.nguyenhuuquang.doanketthucmon.cafe.entity.enums.Status;

import lombok.Data;

@Data
public class TableRequest {
    private Integer number;
    private Integer capacity;
    private Status status;
}

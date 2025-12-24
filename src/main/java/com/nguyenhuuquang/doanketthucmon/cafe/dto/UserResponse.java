package com.nguyenhuuquang.doanketthucmon.cafe.dto;

import com.nguyenhuuquang.doanketthucmon.cafe.entity.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String fullName;
    private Role role;
    private String email;
    private String phone;
    private String imageUrl;
    private Boolean isActive;
}

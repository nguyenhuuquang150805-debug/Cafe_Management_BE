package com.nguyenhuuquang.doanketthucmon.cafe.dto;

import com.nguyenhuuquang.doanketthucmon.cafe.entity.enums.Role;

import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String password;
    private String fullName;
    private Role role;
    private String email;
    private String phone;
    private String imageUrl;
}

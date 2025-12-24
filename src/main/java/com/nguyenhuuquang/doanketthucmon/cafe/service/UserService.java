package com.nguyenhuuquang.doanketthucmon.cafe.service;

import java.util.List;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.UserRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.UserResponse;

public interface UserService {
    UserResponse createUser(UserRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UserRequest request);

    void deleteUser(Long id);
}

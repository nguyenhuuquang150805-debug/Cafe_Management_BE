package com.nguyenhuuquang.doanketthucmon.cafe.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.UserRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.UserResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.User;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.enums.Role;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.UserRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response, "password");
        return response;
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        BeanUtils.copyProperties(request, user);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getRole() == null) {
            user.setRole(Role.STAFF);
        } else {
            try {
                user.setRole(Role.valueOf(request.getRole().toString().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role: " + request.getRole());
            }
        }

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        return convertToResponse(saved);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        User u = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return convertToResponse(u);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        BeanUtils.copyProperties(request, user, "id", "createdAt", "password");

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            try {
                user.setRole(Role.valueOf(request.getRole().toString().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role: " + request.getRole());
            }
        }

        user.setUpdatedAt(LocalDateTime.now());
        return convertToResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

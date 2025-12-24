package com.nguyenhuuquang.doanketthucmon.cafe.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.UserRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.dto.UserResponse;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.User;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.enums.Role;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.UserRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // ✅ Thêm PasswordEncoder

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<User> createUserWithAvatar(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "role", required = false, defaultValue = "USER") String roleString,
            @RequestParam(value = "isActive", required = false, defaultValue = "true") Boolean isActive,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "phone", required = false) String phone) throws IOException {

        Role role = Role.valueOf(roleString.toUpperCase());

        String imageUrl = null;

        if (avatarFile != null && !avatarFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + avatarFile.getOriginalFilename();
            Path uploadPath = Paths.get("src/main/resources/static/uploads");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            imageUrl = fileName;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // ✅ MÃ HÓA MẬT KHẨU
        user.setFullName(fullName);
        user.setRole(role);
        user.setPhone(phone);
        user.setIsActive(isActive);
        user.setImageUrl(imageUrl);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<User> updateUserWithAvatar(
            @PathVariable Long id,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "role", required = false, defaultValue = "USER") String roleString,
            @RequestParam(value = "isActive", required = false, defaultValue = "true") Boolean isActive,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "phone", required = false) String phone) throws IOException {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        Role role = Role.valueOf(roleString.toUpperCase());

        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(role);
        user.setPhone(phone);
        user.setIsActive(isActive);
        user.setUpdatedAt(LocalDateTime.now());

        // ✅ Chỉ cập nhật mật khẩu nếu được gửi từ client VÀ MÃ HÓA
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(password)); // ✅ MÃ HÓA MẬT KHẨU
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + avatarFile.getOriginalFilename();
            Path uploadPath = Paths.get("src/main/resources/static/uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            user.setImageUrl(fileName);
        }

        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
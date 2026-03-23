package com.nguyenhuuquang.doanketthucmon.cafe.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyenhuuquang.doanketthucmon.cafe.dto.RegisterRequest;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.User;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.enums.Role;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.UserRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;

        public AuthController(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil) {
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.jwtUtil = jwtUtil;
        }

        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
                String username = request.get("username");
                String password = request.get("password");

                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                if (!passwordEncoder.matches(password, user.getPassword())) {
                        return ResponseEntity.badRequest().body("Invalid credentials");
                }

                String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
                return ResponseEntity.ok(Map.of("token", token));
        }

        @PostMapping("/register")
        public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
                if (userRepository.existsByUsername(request.getUsername())) {
                        return ResponseEntity.badRequest().body(
                                        Map.of("error", "Username '" + request.getUsername() + "' đã được sử dụng"));
                }

                if (request.getEmail() != null && !request.getEmail().isBlank()
                                && userRepository.existsByEmail(request.getEmail())) {
                        return ResponseEntity.badRequest().body(
                                        Map.of("error", "Email '" + request.getEmail() + "' đã được sử dụng"));
                }

                User user = User.builder()
                                .username(request.getUsername())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .fullName(request.getFullName())
                                .email(request.getEmail())
                                .phone(request.getPhone())
                                .role(Role.STAFF)
                                .isActive(true)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                userRepository.save(user);

                String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
                return ResponseEntity.ok(Map.of(
                                "token", token,
                                "message", "Đăng ký thành công",
                                "username", user.getUsername(),
                                "role", user.getRole().name()));
        }
}
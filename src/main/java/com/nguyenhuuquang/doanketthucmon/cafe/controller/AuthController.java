package com.nguyenhuuquang.doanketthucmon.cafe.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyenhuuquang.doanketthucmon.cafe.entity.User;
import com.nguyenhuuquang.doanketthucmon.cafe.entity.enums.Role;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.UserRepository;
import com.nguyenhuuquang.doanketthucmon.cafe.security.JwtUtil;

@RestController
@CrossOrigin(origins = "*")
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

        @PostMapping("/register")
        public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
                System.out.println("═══════════════════════════════════════════════════");
                System.out.println("🔍 [AUTH] Register request received");
                System.out.println("📧 Email: " + request.get("email"));
                System.out.println("👤 Full Name: " + request.get("fullName"));
                System.out.println("📱 Phone: " + request.get("phone"));
                System.out.println("═══════════════════════════════════════════════════");

                try {
                        String email = request.get("email");
                        String password = request.get("password");
                        String fullName = request.get("fullName");
                        String phone = request.get("phone");

                        // ✅ Validate input
                        if (email == null || email.trim().isEmpty()) {
                                System.err.println("❌ [AUTH] Email is missing");
                                return ResponseEntity.badRequest()
                                                .body(Map.of("error", "Email is required"));
                        }

                        if (password == null || password.trim().isEmpty()) {
                                System.err.println("❌ [AUTH] Password is missing");
                                return ResponseEntity.badRequest()
                                                .body(Map.of("error", "Password is required"));
                        }

                        if (password.length() < 6) {
                                System.err.println("❌ [AUTH] Password too short");
                                return ResponseEntity.badRequest()
                                                .body(Map.of("error", "Password must be at least 6 characters"));
                        }

                        // ✅ Check if email already exists
                        if (userRepository.findByEmail(email).isPresent()) {
                                System.err.println("❌ [AUTH] Email already exists: " + email);
                                return ResponseEntity.status(HttpStatus.CONFLICT)
                                                .body(Map.of("error", "Email already exists"));
                        }

                        // ✅ Check if username already exists (since username = email)
                        if (userRepository.findByUsername(email).isPresent()) {
                                System.err.println("❌ [AUTH] Username already exists: " + email);
                                return ResponseEntity.status(HttpStatus.CONFLICT)
                                                .body(Map.of("error", "Email already exists"));
                        }

                        System.out.println("✅ [AUTH] Creating new user...");

                        // ✅ Create new user
                        User user = new User();
                        user.setEmail(email.trim().toLowerCase()); // Normalize email
                        user.setUsername(email.trim().toLowerCase()); // Username = Email
                        user.setPassword(passwordEncoder.encode(password));
                        user.setFullName(fullName != null ? fullName.trim() : "");
                        user.setPhone(phone != null ? phone.trim() : "");
                        user.setRole(Role.STAFF);
                        user.setIsActive(true);
                        user.setCreatedAt(LocalDateTime.now());
                        user.setUpdatedAt(LocalDateTime.now());

                        System.out.println("💾 [AUTH] Saving user to database...");
                        User savedUser = userRepository.save(user);
                        System.out.println("✅ [AUTH] User saved with ID: " + savedUser.getId());

                        // ✅ Generate token
                        String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getRole().name());
                        System.out.println("🔐 [AUTH] Token generated successfully");

                        System.out.println("═══════════════════════════════════════════════════");
                        System.out.println("✅ [AUTH] Registration successful!");
                        System.out.println("📧 Email: " + savedUser.getEmail());
                        System.out.println("👤 Username: " + savedUser.getUsername());
                        System.out.println("🎭 Role: " + savedUser.getRole());
                        System.out.println("🆔 ID: " + savedUser.getId());
                        System.out.println("═══════════════════════════════════════════════════");

                        return ResponseEntity.status(HttpStatus.CREATED)
                                        .body(Map.of(
                                                        "message", "User registered successfully",
                                                        "token", token,
                                                        "user", Map.of(
                                                                        "id", savedUser.getId(),
                                                                        "email", savedUser.getEmail(),
                                                                        "username", savedUser.getUsername(),
                                                                        "fullName",
                                                                        savedUser.getFullName() != null
                                                                                        ? savedUser.getFullName()
                                                                                        : "",
                                                                        "role", savedUser.getRole().name())));

                } catch (org.springframework.dao.DataIntegrityViolationException e) {
                        System.err.println("❌ [AUTH] Database constraint violation: " + e.getMessage());
                        e.printStackTrace();

                        if (e.getMessage().contains("email")) {
                                return ResponseEntity.status(HttpStatus.CONFLICT)
                                                .body(Map.of("error", "Email already exists"));
                        } else if (e.getMessage().contains("username")) {
                                return ResponseEntity.status(HttpStatus.CONFLICT)
                                                .body(Map.of("error", "Username already exists"));
                        } else if (e.getMessage().contains("users_pkey") || e.getMessage().contains("duplicate key")) {
                                return ResponseEntity.status(HttpStatus.CONFLICT)
                                                .body(Map.of("error",
                                                                "User already exists. Please contact administrator."));
                        }

                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of("error", "Database error: " + e.getMessage()));

                } catch (Exception e) {
                        System.err.println("❌ [AUTH] Registration error: " + e.getMessage());
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of("error", "Registration failed: " + e.getMessage()));
                }
        }

        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
                System.out.println("═══════════════════════════════════════════════════");
                System.out.println("🔍 [AUTH] Login request received");
                System.out.println("📧 Email: " + request.get("email"));
                System.out.println("═══════════════════════════════════════════════════");

                try {
                        String email = request.get("email");
                        String password = request.get("password");

                        // ✅ Validate input
                        if (email == null || email.trim().isEmpty()) {
                                System.err.println("❌ [AUTH] Email is missing");
                                return ResponseEntity.badRequest()
                                                .body(Map.of("error", "Email is required"));
                        }

                        if (password == null || password.trim().isEmpty()) {
                                System.err.println("❌ [AUTH] Password is missing");
                                return ResponseEntity.badRequest()
                                                .body(Map.of("error", "Password is required"));
                        }

                        // ✅ Normalize email
                        String normalizedEmail = email.trim().toLowerCase();
                        System.out.println("🔍 [AUTH] Looking for user with email: " + normalizedEmail);

                        // ✅ Find user by email
                        User user = userRepository.findByEmail(normalizedEmail)
                                        .orElseThrow(() -> {
                                                System.err.println("❌ [AUTH] User not found with email: "
                                                                + normalizedEmail);
                                                return new RuntimeException("Invalid credentials");
                                        });

                        System.out.println("✅ [AUTH] User found: " + user.getEmail());
                        System.out.println("👤 [AUTH] Username: " + user.getUsername());
                        System.out.println("🎭 [AUTH] Role: " + user.getRole());

                        // ✅ Validate password
                        if (!passwordEncoder.matches(password, user.getPassword())) {
                                System.err.println("❌ [AUTH] Invalid password for user: " + normalizedEmail);
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(Map.of("error", "Invalid credentials"));
                        }

                        System.out.println("✅ [AUTH] Password validated");

                        // ✅ Check if account is active
                        if (!user.getIsActive()) {
                                System.err.println("❌ [AUTH] Account inactive: " + normalizedEmail);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(Map.of("error", "Account is inactive"));
                        }

                        System.out.println("✅ [AUTH] Account is active");

                        // ✅ Generate token with USERNAME (which equals email in your system)
                        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
                        System.out.println("🔐 [AUTH] Token generated with username: " + user.getUsername());

                        System.out.println("═══════════════════════════════════════════════════");
                        System.out.println("✅ [AUTH] Login successful!");
                        System.out.println("📧 Email: " + user.getEmail());
                        System.out.println("👤 Username: " + user.getUsername());
                        System.out.println("🎭 Role: " + user.getRole());
                        System.out.println("🆔 ID: " + user.getId());
                        System.out.println("═══════════════════════════════════════════════════");

                        return ResponseEntity.ok(Map.of(
                                        "token", token,
                                        "user", Map.of(
                                                        "id", user.getId(),
                                                        "email", user.getEmail(),
                                                        "username", user.getUsername(),
                                                        "fullName",
                                                        user.getFullName() != null ? user.getFullName() : "",
                                                        "role", user.getRole().name())));

                } catch (Exception e) {
                        System.err.println("❌ [AUTH] Login error: " + e.getMessage());
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(Map.of("error", "Invalid credentials"));
                }
        }

        // ❌ XỬ LÝ SAI METHOD (GET thay vì POST)
        @GetMapping("/register")
        public ResponseEntity<?> registerGetMethod() {
                System.err.println("❌ [AUTH] Wrong HTTP method - GET instead of POST for /register");
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                                .body(Map.of(
                                                "error", "❌ WRONG HTTP METHOD!",
                                                "message",
                                                "You are using GET method. Registration requires POST method.",
                                                "correctMethod", "POST",
                                                "endpoint", "/api/auth/register",
                                                "howToFix", Map.of(
                                                                "step1", "Change HTTP method from GET to POST",
                                                                "step2", "Add header: Content-Type: application/json",
                                                                "step3",
                                                                "Add JSON body with email, password, fullName, phone"),
                                                "exampleCurl",
                                                "curl -X POST https://cafemanagementbe-production.up.railway.app/api/auth/register -H \"Content-Type: application/json\" -d '{\"email\":\"test@gmail.com\",\"password\":\"123456\",\"fullName\":\"Test User\",\"phone\":\"0123456789\"}'"));
        }

        @GetMapping("/login")
        public ResponseEntity<?> loginGetMethod() {
                System.err.println("❌ [AUTH] Wrong HTTP method - GET instead of POST for /login");
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                                .body(Map.of(
                                                "error", "❌ WRONG HTTP METHOD!",
                                                "message", "You are using GET method. Login requires POST method.",
                                                "correctMethod", "POST",
                                                "endpoint", "/api/auth/login"));
        }
}
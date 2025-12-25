package com.nguyenhuuquang.doanketthucmon.cafe.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "☕ Coffee Management API is running!");
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("version", "1.0.0");

        // 📋 API Endpoints với hướng dẫn chi tiết
        response.put("endpoints", Map.of(
                "health", Map.of(
                        "url", "/health",
                        "method", "GET",
                        "description", "Check API health status"),
                "register", Map.of(
                        "url", "/api/auth/register",
                        "method", "POST ⚠️",
                        "description", "Register new user",
                        "contentType", "application/json",
                        "body", Map.of(
                                "email", "test@gmail.com",
                                "password", "123456",
                                "fullName", "Test User",
                                "phone", "0123456789")),
                "login", Map.of(
                        "url", "/api/auth/login",
                        "method", "POST ⚠️",
                        "description", "Login user",
                        "contentType", "application/json",
                        "body", Map.of(
                                "email", "test@gmail.com",
                                "password", "123456"))));

        response.put("⚠️ IMPORTANT", "Registration and Login endpoints require POST method, not GET!");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "coffee-management");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}
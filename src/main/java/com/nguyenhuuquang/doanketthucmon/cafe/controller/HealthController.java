package com.nguyenhuuquang.doanketthucmon.cafe.controller;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health Check Controller for Railway deployment
 * Railway uses /health endpoint to verify the application is running
 */
@RestController
public class HealthController {

    @Autowired
    private DataSource dataSource;

    /**
     * Root endpoint - Basic info about the API
     */
    @GetMapping("/")
    public ResponseEntity<?> root() {
        System.out.println("🌍 [HEALTH] Root endpoint accessed");
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Cafe Management API",
                "message", "API is running successfully",
                "timestamp", LocalDateTime.now(),
                "version", "1.0.0"));
    }

    /**
     * Health endpoint - Required by Railway
     * This endpoint MUST return 200 OK for Railway to consider the app healthy
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        System.out.println("💓 [HEALTH] Health check endpoint accessed");

        Map<String, Object> healthStatus = new HashMap<>();
        healthStatus.put("status", "UP");
        healthStatus.put("timestamp", LocalDateTime.now());

        // Check database connection
        try {
            Connection connection = dataSource.getConnection();
            boolean isValid = connection.isValid(1);
            connection.close();

            healthStatus.put("database", Map.of(
                    "status", isValid ? "UP" : "DOWN",
                    "type", "PostgreSQL"));

            System.out.println("✅ [HEALTH] Database connection: OK");
        } catch (Exception e) {
            System.err.println("❌ [HEALTH] Database connection failed: " + e.getMessage());
            healthStatus.put("database", Map.of(
                    "status", "DOWN",
                    "error", e.getMessage()));
        }

        return ResponseEntity.ok(healthStatus);
    }

    /**
     * Ping endpoint - Simple alive check
     */
    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        System.out.println("🏓 [HEALTH] Ping endpoint accessed");
        return ResponseEntity.ok(Map.of(
                "message", "pong",
                "timestamp", LocalDateTime.now()));
    }

    /**
     * API Info endpoint - Detailed information
     */
    @GetMapping("/api/info")
    public ResponseEntity<?> info() {
        System.out.println("ℹ️ [HEALTH] Info endpoint accessed");
        return ResponseEntity.ok(Map.of(
                "service", "Cafe Management System",
                "version", "1.0.0",
                "description", "Backend API for Cafe Management",
                "endpoints", Map.of(
                        "auth", "/api/auth/**",
                        "users", "/api/users/**",
                        "products", "/api/products/**",
                        "orders", "/api/orders/**",
                        "tables", "/api/tables/**"),
                "timestamp", LocalDateTime.now()));
    }
}
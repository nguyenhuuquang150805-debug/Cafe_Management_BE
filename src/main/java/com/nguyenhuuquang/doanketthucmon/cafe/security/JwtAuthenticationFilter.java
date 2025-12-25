package com.nguyenhuuquang.doanketthucmon.cafe.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MyUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, MyUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // 🔍 DEBUG: Log chi tiết
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("🔍 [JWT Filter] Method: " + method);
        System.out.println("🔍 [JWT Filter] Path: " + path);
        System.out.println("🔍 [JWT Filter] Content-Type: " + request.getHeader("Content-Type"));
        System.out.println("🔍 [JWT Filter] User-Agent: " + request.getHeader("User-Agent"));
        System.out.println("🔍 [JWT Filter] Origin: " + request.getHeader("Origin"));

        // Log tất cả headers
        System.out.println("🔍 [JWT Filter] All Headers:");
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println("  " + headerName + ": " + request.getHeader(headerName));
        }
        System.out.println("═══════════════════════════════════════════════════");

        // Rest of your filter code...
        if (path.equals("/") ||
                path.equals("/health") ||
                path.equals("/ping") ||
                path.startsWith("/actuator") ||
                path.startsWith("/api/auth") ||
                path.startsWith("/uploads") ||
                path.startsWith("/api/payment") ||
                path.equals("/favicon.ico")) {
            System.out.println("✅ [JWT Filter] Public endpoint - bypass");
            filterChain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // Trích xuất JWT từ header
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                System.out.println("✅ [JWT Filter] Extracted username: " + username);
            } catch (Exception e) {
                System.err.println("❌ [JWT Filter] JWT extraction error: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ [JWT Filter] No Authorization header or invalid format");
        }

        // Validate JWT và set authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt)) {
                    String tokenUsername = jwtUtil.extractUsername(jwt);
                    if (tokenUsername.equals(userDetails.getUsername())) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("✅ [JWT Filter] Authentication successful for: " + username);
                    }
                } else {
                    System.err.println("❌ [JWT Filter] Invalid token");
                }
            } catch (Exception e) {
                System.err.println("❌ [JWT Filter] Authentication error: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
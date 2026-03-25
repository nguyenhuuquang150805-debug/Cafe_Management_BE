package com.nguyenhuuquang.doanketthucmon.cafe.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;

        public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
                this.jwtAuthFilter = jwtAuthFilter;
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOriginPatterns(List.of("*")); // hoặc chỉ định FE URL cụ thể
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(authz -> authz
                                                .requestMatchers("/health", "/", "/api/auth/**", "/uploads/**",
                                                                "/api/payment/**", "/ws/**")
                                                .permitAll()

                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/categories/**",
                                                                "/api/products/**",
                                                                "/api/promotions/**")
                                                .permitAll()

                                                .requestMatchers(HttpMethod.GET, "/api/bills/**").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/bills/**")
                                                .hasAnyRole("ADMIN", "STAFF")
                                                .requestMatchers(HttpMethod.PUT, "/api/bills/**")
                                                .hasAnyRole("ADMIN", "STAFF")
                                                .requestMatchers(HttpMethod.DELETE, "/api/bills/**").hasRole("ADMIN")

                                                .requestMatchers(HttpMethod.GET, "/api/tables/**", "/api/orders/**")
                                                .hasAnyRole("ADMIN", "STAFF", "EMPLOYEE")
                                                .requestMatchers(HttpMethod.POST, "/api/tables/**", "/api/orders/**")
                                                .hasAnyRole("ADMIN", "STAFF")
                                                .requestMatchers(HttpMethod.PUT, "/api/tables/**", "/api/orders/**")
                                                .hasAnyRole("ADMIN", "STAFF")
                                                .requestMatchers(HttpMethod.DELETE, "/api/tables/**", "/api/orders/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/categories/**", "/api/products/**",
                                                                "/api/promotions/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT,
                                                                "/api/categories/**", "/api/products/**",
                                                                "/api/promotions/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE,
                                                                "/api/categories/**", "/api/products/**",
                                                                "/api/promotions/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/users/**").hasAnyRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")

                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}
package com.nguyenhuuquang.doanketthucmon.cafe.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nguyenhuuquang.doanketthucmon.cafe.entity.User;
import com.nguyenhuuquang.doanketthucmon.cafe.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        System.out.println("🔍 [UserDetailsService] Loading user: " + usernameOrEmail);

        // ✅ TÌM THEO USERNAME HOẶC EMAIL
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> {
                    System.err.println("❌ [UserDetailsService] User not found: " + usernameOrEmail);
                    return new UsernameNotFoundException("User not found: " + usernameOrEmail);
                });

        System.out.println("✅ [UserDetailsService] User found: " + user.getUsername() + ", Role: " + user.getRole());

        // ✅ KIỂM TRA TÀI KHOẢN CÓ ACTIVE KHÔNG
        if (!user.getIsActive()) {
            System.err.println("❌ [UserDetailsService] User is inactive: " + user.getUsername());
            throw new UsernameNotFoundException("User is inactive");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }
}
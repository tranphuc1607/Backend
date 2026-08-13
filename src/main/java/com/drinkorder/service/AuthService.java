package com.drinkorder.service;

import com.drinkorder.dto.auth.AuthResponse;
import com.drinkorder.dto.auth.LoginRequest;
import com.drinkorder.dto.auth.RegisterRequest;
import com.drinkorder.dto.user.UserResponse;
import com.drinkorder.entity.Role;
import com.drinkorder.entity.User;
import com.drinkorder.exception.BadRequestException;
import com.drinkorder.repository.UserRepository;
import com.drinkorder.security.JwtService;
import com.drinkorder.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email đã được sử dụng");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .phone(request.getPhone())
                .role(Role.USER)
                .enabled(true)
                .build();

        User saved = userRepository.save(user);
        SecurityUser securityUser = new SecurityUser(saved);
        String token = jwtService.generateToken(securityUser);
        return AuthResponse.of(token, UserResponse.fromEntity(saved));
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        String token = jwtService.generateToken(securityUser);
        return AuthResponse.of(token, UserResponse.fromEntity(securityUser.getUser()));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}

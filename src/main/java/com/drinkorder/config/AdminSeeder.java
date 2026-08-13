package com.drinkorder.config;

import com.drinkorder.entity.Role;
import com.drinkorder.entity.User;
import com.drinkorder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Tạo tài khoản ADMIN lần đầu chạy (nếu chưa có admin nào).
 *
 * Thông tin lấy từ app.admin trong application.yml, ghi đè được bằng biến môi trường
 * APP_ADMIN_EMAIL / APP_ADMIN_PASSWORD (Spring Boot relaxed binding).
 * Vì API /auth/register luôn tạo role USER, đây là cách duy nhất để có ADMIN.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminSeedProperties adminProperties;

    @Bean
    CommandLineRunner seedAdminAccount() {
        return args -> {
            String rawEmail = adminProperties.getEmail();
            String password = adminProperties.getPassword();

            if (rawEmail == null || rawEmail.isBlank() || password == null || password.isBlank()) {
                log.warn("Bỏ qua tạo admin: app.admin.email hoặc app.admin.password chưa được cấu hình");
                return;
            }

            // AuthService hạ email về chữ thường khi login, nên seed cùng dạng để khớp lúc tra cứu.
            String email = rawEmail.trim().toLowerCase();

            if (userRepository.existsByRole(Role.ADMIN)) {
                return;
            }

            // Email đã thuộc về user khác: không ghi đè, tránh vi phạm unique constraint lúc boot.
            if (userRepository.existsByEmail(email)) {
                log.warn("Bỏ qua tạo admin: email {} đã được dùng bởi tài khoản khác", email);
                return;
            }

            String fullName = adminProperties.getFullName();
            if (fullName == null || fullName.isBlank()) {
                fullName = "Quản trị viên";
            }

            userRepository.save(User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .fullName(fullName.trim())
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build());

            log.info("Đã tạo tài khoản admin: {}", email);
        };
    }
}

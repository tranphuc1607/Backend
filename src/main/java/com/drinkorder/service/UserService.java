package com.drinkorder.service;

import com.drinkorder.dto.user.UserResponse;
import com.drinkorder.dto.user.UserUpdateRequest;
import com.drinkorder.entity.User;
import com.drinkorder.exception.BadRequestException;
import com.drinkorder.exception.ResourceNotFoundException;
import com.drinkorder.repository.UserRepository;
import com.drinkorder.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Lấy thông tin user đang đăng nhập
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile() {
        return UserResponse.fromEntity(getCurrentUserEntity());
    }

    // Cập nhật thông tin cá nhân
    @Transactional
    public UserResponse updateCurrentUserProfile(UserUpdateRequest request) {
        User user = getCurrentUserEntity();
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }
        return UserResponse.fromEntity(userRepository.save(user));
    }

    // Admin: Lấy danh sách tất cả users
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Admin: Vô hiệu hóa tài khoản user
    @Transactional
    public void disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        // Không cho phép disable admin
        if (user.getRole().name().equals("ADMIN")) {
            throw new BadRequestException("Không thể vô hiệu hóa tài khoản admin");
        }

        user.setEnabled(false);
        userRepository.save(user);
    }

    public User getCurrentUserEntity() {
        String email = SecurityUtils.getCurrentUserEmail();
        if (email == null) {
            throw new ResourceNotFoundException("Không tìm thấy phiên đăng nhập");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }
}

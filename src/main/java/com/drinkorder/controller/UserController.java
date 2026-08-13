package com.drinkorder.controller;

import com.drinkorder.dto.common.ApiResponse;
import com.drinkorder.dto.user.UserResponse;
import com.drinkorder.dto.user.UserUpdateRequest;
import com.drinkorder.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getCurrentUserProfile()));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(@Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật hồ sơ thành công", userService.updateCurrentUserProfile(request)));
    }
}

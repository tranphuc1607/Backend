package com.drinkorder.controller;

import com.drinkorder.dto.address.AddressRequest;
import com.drinkorder.dto.address.AddressResponse;
import com.drinkorder.dto.common.ApiResponse;
import com.drinkorder.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller quản lý địa chỉ giao hàng.
 * Base URL: /api/v1/addresses
 * Tất cả API đều yêu cầu đăng nhập (JWT).
 */
@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // GET /addresses → Lấy danh sách địa chỉ của user đang đăng nhập
    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses() {
        return ResponseEntity.ok(ApiResponse.ok(addressService.getMyAddresses()));
    }

    // POST /addresses → Thêm địa chỉ mới
    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @Valid @RequestBody AddressRequest request) {
        AddressResponse data = addressService.addAddress(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Thêm địa chỉ thành công", data));
    }

    // PUT /addresses/{id} → Cập nhật địa chỉ
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật thành công",
                addressService.updateAddress(id, request)));
    }

    // DELETE /addresses/{id} → Xóa địa chỉ
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa địa chỉ thành công", null));
    }

    // PUT /addresses/{id}/default → Đặt làm địa chỉ mặc định
    @PutMapping("/{id}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefault(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Đặt địa chỉ mặc định thành công",
                addressService.setDefault(id)));
    }
}

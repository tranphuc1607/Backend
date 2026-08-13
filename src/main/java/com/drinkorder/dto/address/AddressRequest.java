package com.drinkorder.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * DTO nhận dữ liệu khi tạo hoặc cập nhật địa chỉ.
 */
@Data
public class AddressRequest {

    @NotBlank(message = "Tên người nhận không được để trống")
    private String recipientName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{9,11}$", message = "Số điện thoại không hợp lệ")
    private String recipientPhone;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String street;

    private String ward;
    private String district;

    @NotBlank(message = "Thành phố không được để trống")
    private String city;

    // Đặt làm địa chỉ mặc định không?
    private Boolean isDefault = false;
}

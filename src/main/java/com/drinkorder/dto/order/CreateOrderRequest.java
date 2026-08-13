package com.drinkorder.dto.order;

import com.drinkorder.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO nhận dữ liệu khi user đặt hàng.
 * User cần chọn địa chỉ giao hàng và phương thức thanh toán.
 */
@Data
public class CreateOrderRequest {

    @NotNull(message = "Vui lòng chọn địa chỉ giao hàng")
    private Long addressId;

    @NotNull(message = "Vui lòng chọn phương thức thanh toán")
    private PaymentMethod paymentMethod;

    // Ghi chú thêm (ví dụ: ít đường, nhiều đá)
    private String note;
}

package com.drinkorder.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO nhận dữ liệu khi thêm sản phẩm vào giỏ hàng.
 */
@Data
public class AddToCartRequest {

    @NotNull(message = "Vui lòng chọn sản phẩm")
    private Long productId;

    @NotNull(message = "Vui lòng chọn size")
    private Long productSizeId;

    // toppingId có thể null (không bắt buộc chọn topping)
    private Long toppingId;

    @NotNull(message = "Vui lòng nhập số lượng")
    @Min(value = 1, message = "Số lượng tối thiểu là 1")
    private Integer quantity;
}

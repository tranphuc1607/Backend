package com.drinkorder.dto.order;

import com.drinkorder.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO nhận trạng thái mới khi admin cập nhật đơn hàng.
 */
@Data
public class UpdateOrderStatusRequest {

    @NotNull(message = "Trạng thái không được để trống")
    private OrderStatus status;
}

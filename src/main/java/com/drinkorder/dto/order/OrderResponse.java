package com.drinkorder.dto.order;

import com.drinkorder.entity.Order;
import com.drinkorder.entity.OrderStatus;
import com.drinkorder.entity.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO trả về thông tin đầy đủ của 1 đơn hàng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private Long userId;
    private String userFullName;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String statusLabel;       // Tên tiếng Việt của trạng thái
    private PaymentMethod paymentMethod;
    private String paymentMethodLabel;
    private String deliveryAddress;
    private String recipientName;
    private String recipientPhone;
    private String note;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    public static OrderResponse fromEntity(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userFullName(order.getUser().getFullName())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .statusLabel(getStatusLabel(order.getStatus()))
                .paymentMethod(order.getPaymentMethod())
                .paymentMethodLabel(getPaymentLabel(order.getPaymentMethod()))
                .deliveryAddress(order.getDeliveryAddress())
                .recipientName(order.getRecipientName())
                .recipientPhone(order.getRecipientPhone())
                .note(order.getNote())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream()
                        .map(OrderItemResponse::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }

    // Chuyển enum thành tên tiếng Việt hiển thị
    private static String getStatusLabel(OrderStatus status) {
        return switch (status) {
            case PENDING -> "Chờ xác nhận";
            case CONFIRMED -> "Đã xác nhận";
            case PREPARING -> "Đang pha chế";
            case DELIVERING -> "Đang giao hàng";
            case DELIVERED -> "Đã giao thành công";
            case CANCELLED -> "Đã hủy";
        };
    }

    private static String getPaymentLabel(PaymentMethod method) {
        return switch (method) {
            case COD -> "Thanh toán khi nhận hàng";
            case MOMO -> "Ví MoMo";
        };
    }
}

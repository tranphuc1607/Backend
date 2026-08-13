package com.drinkorder.dto.order;

import com.drinkorder.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO trả về 1 dòng trong đơn hàng.
 * Bao gồm đầy đủ thông tin để hiển thị lịch sử đơn hàng trong Flutter:
 * - Tên sản phẩm, size, topping tại thời điểm đặt (snapshot)
 * - Giá tại thời điểm đặt (không thay đổi dù sau này product thay đổi giá)
 * - Ảnh sản phẩm (nếu product chưa bị xóa)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private Long id;
    private Long productId;          // null nếu product đã bị xóa
    private String productName;      // snapshot tên lúc đặt
    private String productImageUrl;  // ảnh hiện tại (nullable)
    private String sizeName;         // snapshot size lúc đặt
    private String toppingName;      // snapshot topping lúc đặt (null nếu không có)
    private BigDecimal unitPrice;    // giá 1 đơn vị lúc đặt
    private Integer quantity;
    private BigDecimal subtotal;     // = unitPrice * quantity

    public static OrderItemResponse fromEntity(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProductName())
                // Lấy ảnh từ product hiện tại (nếu product còn tồn tại)
                .productImageUrl(item.getProduct() != null ? item.getProduct().getImageUrl() : null)
                .sizeName(item.getSizeName())
                .toppingName(item.getToppingName())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
    }
}

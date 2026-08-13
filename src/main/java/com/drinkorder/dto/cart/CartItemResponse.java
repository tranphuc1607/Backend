package com.drinkorder.dto.cart;

import com.drinkorder.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO trả về 1 dòng trong giỏ hàng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {

    private Long id;            // CartItem ID
    private Long productId;
    private String productName;
    private String productImageUrl;
    private Long productSizeId;
    private String sizeName;
    private Long toppingId;     // null nếu không chọn topping
    private String toppingName; // null nếu không chọn topping
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal; // = unitPrice * quantity

    public static CartItemResponse fromEntity(CartItem item) {
        BigDecimal subtotal = item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productImageUrl(item.getProduct().getImageUrl())
                .productSizeId(item.getProductSize().getId())
                .sizeName(item.getProductSize().getSizeName())
                .toppingId(item.getTopping() != null ? item.getTopping().getId() : null)
                .toppingName(item.getTopping() != null ? item.getTopping().getName() : null)
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .build();
    }
}

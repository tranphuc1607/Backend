package com.drinkorder.dto.cart;

import com.drinkorder.entity.Cart;
import com.drinkorder.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO trả về thông tin giỏ hàng bao gồm tất cả items và tổng tiền.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

    private Long id;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;   // Tổng tiền toàn giỏ
    private int totalItems;           // Tổng số sản phẩm (tính theo quantity)

    public static CartResponse fromEntity(Cart cart) {
        List<CartItemResponse> itemDtos = cart.getItems().stream()
                .map(CartItemResponse::fromEntity)
                .collect(Collectors.toList());

        // Tính tổng tiền
        BigDecimal total = itemDtos.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Đếm tổng số lượng
        int totalQty = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        return CartResponse.builder()
                .id(cart.getId())
                .items(itemDtos)
                .totalAmount(total)
                .totalItems(totalQty)
                .build();
    }
}

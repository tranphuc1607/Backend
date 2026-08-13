package com.drinkorder.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Mỗi CartItem là 1 dòng trong giỏ hàng:
 * - 1 sản phẩm + 1 size + (tùy chọn) 1 topping + số lượng
 * Lưu ý: 1 giỏ hàng có thể có nhiều dòng với cùng product nhưng size/topping khác nhau.
 */
@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem extends BaseEntity {

    // Thuộc giỏ hàng nào
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // Sản phẩm nào
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Size nào (M, L, XL...)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_size_id", nullable = false)
    private ProductSize productSize;

    // Topping (có thể null nếu không chọn topping)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topping_id")
    private Topping topping;

    // Số lượng
    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    // Lưu giá tại thời điểm thêm vào giỏ (tránh thay đổi giá sau)
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;
}

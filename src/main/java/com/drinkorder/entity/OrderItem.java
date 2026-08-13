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
 * Mỗi OrderItem là 1 dòng trong đơn hàng.
 * Lưu đủ thông tin tên sản phẩm, size, topping tại thời điểm đặt
 * (dù sau này product bị xóa thì lịch sử vẫn còn).
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    // Thuộc đơn hàng nào
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Tham chiếu đến product (nullable vì product có thể bị xóa)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // Lưu tên sản phẩm tại thời điểm đặt
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    // Lưu tên size tại thời điểm đặt
    @Column(name = "size_name", nullable = false, length = 20)
    private String sizeName;

    // Lưu tên topping (null nếu không chọn)
    @Column(name = "topping_name", length = 100)
    private String toppingName;

    // Giá 1 đơn vị tại thời điểm đặt
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    // Số lượng đặt
    @Column(nullable = false)
    private Integer quantity;

    // Tổng tiền dòng này = unitPrice * quantity
    @Column(name = "subtotal", nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;
}

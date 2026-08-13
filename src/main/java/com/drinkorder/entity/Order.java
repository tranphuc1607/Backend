package com.drinkorder.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity đơn hàng.
 * Khi user đặt hàng, dữ liệu giỏ hàng sẽ được sao chép vào bảng orders và order_items.
 * Địa chỉ giao hàng được copy thành string (để tránh thay đổi khi user sửa địa chỉ).
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    // Đơn hàng của user nào
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Tổng tiền đơn hàng (tính từ các OrderItem)
    @Column(name = "total_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;

    // Trạng thái đơn hàng
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    // Phương thức thanh toán
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.COD;

    // Địa chỉ giao hàng - lưu thành string để giữ lịch sử
    @Column(name = "delivery_address", nullable = false, length = 500)
    private String deliveryAddress;

    // Tên người nhận (copy từ địa chỉ)
    @Column(name = "recipient_name", nullable = false, length = 150)
    private String recipientName;

    // SĐT người nhận (copy từ địa chỉ)
    @Column(name = "recipient_phone", nullable = false, length = 20)
    private String recipientPhone;

    // Ghi chú của khách hàng
    @Column(columnDefinition = "TEXT")
    private String note;

    // Danh sách sản phẩm trong đơn
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
}

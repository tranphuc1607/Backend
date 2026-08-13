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

/**
 * Entity lưu địa chỉ giao hàng của người dùng.
 * Mỗi user có thể có nhiều địa chỉ, và đánh dấu 1 địa chỉ mặc định.
 */
@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends BaseEntity {

    // Địa chỉ thuộc về user nào
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Tên người nhận
    @Column(name = "recipient_name", nullable = false, length = 150)
    private String recipientName;

    // Số điện thoại người nhận
    @Column(name = "recipient_phone", nullable = false, length = 20)
    private String recipientPhone;

    // Địa chỉ chi tiết (số nhà, đường)
    @Column(nullable = false, length = 300)
    private String street;

    // Phường/xã
    @Column(length = 100)
    private String ward;

    // Quận/huyện
    @Column(length = 100)
    private String district;

    // Tỉnh/thành phố
    @Column(length = 100)
    private String city;

    // Đây có phải địa chỉ mặc định không?
    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;
}

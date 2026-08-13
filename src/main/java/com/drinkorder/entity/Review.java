package com.drinkorder.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity đánh giá sản phẩm.
 * Mỗi user chỉ đánh giá 1 lần cho 1 sản phẩm (unique constraint).
 * Rating từ 1 đến 5.
 */
@Entity
@Table(
        name = "reviews",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {

    // User đánh giá
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Sản phẩm được đánh giá
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Điểm đánh giá từ 1 đến 5
    @Column(nullable = false)
    private Integer rating;

    // Nội dung bình luận (có thể để trống)
    @Column(columnDefinition = "TEXT")
    private String comment;
}

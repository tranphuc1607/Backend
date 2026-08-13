package com.drinkorder.repository;

import com.drinkorder.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho đánh giá sản phẩm.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Lấy tất cả đánh giá của 1 sản phẩm
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

    // Tìm đánh giá của 1 user cho 1 sản phẩm cụ thể
    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);

    // Kiểm tra user đã đánh giá sản phẩm này chưa
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    // Tính điểm trung bình của sản phẩm
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

    // Đếm số lượng đánh giá của sản phẩm
    long countByProductId(Long productId);
}

package com.drinkorder.repository;

import com.drinkorder.entity.Order;
import com.drinkorder.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository cho đơn hàng.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Lấy danh sách đơn hàng của 1 user (mới nhất trước)
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Lấy tất cả đơn hàng có phân trang (dành cho admin)
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Lấy đơn hàng theo trạng thái (dành cho admin)
    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

    /**
     * Tra cứu đơn cho trang quản trị: lọc theo trạng thái và/hoặc khoảng thời gian.
     * Tham số nào null thì bỏ qua điều kiện đó, nên một query dùng được cho mọi
     * kết hợp (tất cả trạng thái + một ngày, một trạng thái + mọi ngày, ...).
     *
     * Khoảng thời gian nửa mở [from, to): tránh sai lệch ở mốc 23:59:59.999
     * mà điều kiện <= cuối ngày dễ mắc phải.
     */
    @Query("""
            SELECT o FROM Order o
            WHERE (:status IS NULL OR o.status = :status)
              AND (:from IS NULL OR o.createdAt >= :from)
              AND (:to IS NULL OR o.createdAt < :to)
            ORDER BY o.createdAt DESC
            """)
    Page<Order> searchForAdmin(
            @Param("status") OrderStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    // Tìm đơn hàng cụ thể của user (để user không xem được đơn của người khác)
    Optional<Order> findByIdAndUserId(Long id, Long userId);

    // Thống kê: đếm đơn hàng theo trạng thái
    long countByStatus(OrderStatus status);

    // Thống kê: tính tổng doanh thu (chỉ đơn DELIVERED)
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'DELIVERED'")
    BigDecimal sumRevenue();

    // Thống kê: doanh thu theo tháng
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE o.status = 'DELIVERED' AND o.createdAt BETWEEN :start AND :end")
    BigDecimal sumRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}

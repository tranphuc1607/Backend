package com.drinkorder.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO trả về số liệu thống kê cho trang Dashboard Admin.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStats {

    // Tổng số đơn hàng
    private long totalOrders;

    // Số đơn hàng đang chờ xử lý
    private long pendingOrders;

    // Số đơn hàng đang giao
    private long deliveringOrders;

    // Số đơn hàng đã giao thành công
    private long deliveredOrders;

    // Số đơn hàng đã hủy
    private long cancelledOrders;

    // Tổng doanh thu (chỉ tính đơn DELIVERED)
    private BigDecimal totalRevenue;

    // Tổng số người dùng
    private long totalUsers;

    // Doanh thu theo tháng (key: "YYYY-MM", value: doanh thu)
    private Map<String, BigDecimal> monthlyRevenue;
}

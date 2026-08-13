package com.drinkorder.service;

import com.drinkorder.dto.admin.DashboardStats;
import com.drinkorder.entity.OrderStatus;
import com.drinkorder.repository.OrderRepository;
import com.drinkorder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Service tính toán thống kê cho trang Admin Dashboard.
 * Cung cấp: tổng đơn, doanh thu, phân loại trạng thái đơn, doanh thu theo tháng.
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public DashboardStats getDashboardStats() {
        // Đếm đơn hàng theo từng trạng thái
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long deliveringOrders = orderRepository.countByStatus(OrderStatus.DELIVERING);
        long deliveredOrders = orderRepository.countByStatus(OrderStatus.DELIVERED);
        long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);

        // Tổng doanh thu
        BigDecimal totalRevenue = orderRepository.sumRevenue();

        // Tổng users
        long totalUsers = userRepository.count();

        // Doanh thu 6 tháng gần nhất
        Map<String, BigDecimal> monthlyRevenue = new LinkedHashMap<>();
        YearMonth now = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth month = now.minusMonths(i);
            LocalDateTime start = month.atDay(1).atStartOfDay();
            LocalDateTime end = month.atEndOfMonth().atTime(23, 59, 59);
            BigDecimal revenue = orderRepository.sumRevenueBetween(start, end);
            monthlyRevenue.put(month.toString(), revenue);
        }

        return DashboardStats.builder()
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .deliveringOrders(deliveringOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .totalRevenue(totalRevenue)
                .totalUsers(totalUsers)
                .monthlyRevenue(monthlyRevenue)
                .build();
    }
}

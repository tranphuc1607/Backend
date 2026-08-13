package com.drinkorder.controller;

import com.drinkorder.dto.common.ApiResponse;
import com.drinkorder.dto.order.CreateOrderRequest;
import com.drinkorder.dto.order.OrderResponse;
import com.drinkorder.dto.order.UpdateOrderStatusRequest;
import com.drinkorder.entity.OrderStatus;
import com.drinkorder.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import java.util.List;

/**
 * Controller quản lý đơn hàng.
 * Base URL: /api/v1/orders
 *
 * USER APIs:
 *   GET    /orders/my          → Lấy danh sách đơn hàng của mình
 *   GET    /orders/my/{id}     → Chi tiết 1 đơn hàng
 *   POST   /orders             → Đặt hàng
 *   PUT    /orders/{id}/cancel → Hủy đơn hàng
 *
 * ADMIN APIs:
 *   GET    /orders/admin           → Tất cả đơn hàng (phân trang)
 *   GET    /orders/admin/{id}      → Chi tiết đơn hàng
 *   PUT    /orders/admin/{id}/status → Cập nhật trạng thái
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ======= USER APIs =======

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders() {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getMyOrders()));
    }

    @GetMapping("/my/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getMyOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getMyOrderById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse data = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Đặt hàng thành công", data));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Hủy đơn hàng thành công",
                orderService.cancelOrder(id)));
    }

    // ======= ADMIN APIs =======

    /**
     * Danh sách đơn cho admin.
     *
     * status : lọc theo trạng thái, bỏ trống = tất cả.
     * date   : lọc đúng một ngày (yyyy-MM-dd).
     * from/to: lọc một khoảng ngày, bao gồm cả hai đầu.
     *
     * Nếu truyền date thì bỏ qua from/to. Không truyền gì = mọi ngày.
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(
                orderService.searchOrders(status, date, from, to, pageable)));
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getOrderById(id)));
    }

    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật trạng thái thành công",
                orderService.updateOrderStatus(id, request)));
    }
}

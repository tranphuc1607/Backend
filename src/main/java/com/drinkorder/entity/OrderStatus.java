package com.drinkorder.entity;

/**
 * Trạng thái đơn hàng theo luồng:
 * PENDING (chờ xác nhận) → CONFIRMED (đã xác nhận) → PREPARING (đang pha chế)
 * → DELIVERING (đang giao) → DELIVERED (đã giao) → CANCELLED (đã hủy)
 *
 * User chỉ có thể hủy khi đơn đang PENDING.
 * Admin có thể cập nhật bất kỳ trạng thái nào.
 */
public enum OrderStatus {
    PENDING,      // Chờ xác nhận
    CONFIRMED,    // Đã xác nhận
    PREPARING,    // Đang chuẩn bị
    DELIVERING,   // Đang giao hàng
    DELIVERED,    // Đã giao thành công
    CANCELLED     // Đã hủy
}

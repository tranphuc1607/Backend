package com.drinkorder.entity;

/**
 * Phương thức thanh toán được hỗ trợ.
 * COD: Cash on delivery (thanh toán khi nhận hàng)
 * MOMO: Ví điện tử MoMo (demo - trong BTL chưa tích hợp thực)
 */
public enum PaymentMethod {
    COD,   // Thanh toán tiền mặt khi nhận hàng
    MOMO   // Thanh toán qua ví MoMo
}

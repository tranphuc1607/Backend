package com.drinkorder.controller;

import com.drinkorder.dto.cart.AddToCartRequest;
import com.drinkorder.dto.cart.CartResponse;
import com.drinkorder.dto.cart.UpdateCartItemRequest;
import com.drinkorder.dto.common.ApiResponse;
import com.drinkorder.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller quản lý giỏ hàng.
 * Base URL: /api/v1/cart
 * Tất cả API đều yêu cầu đăng nhập.
 */
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // GET /cart → Lấy giỏ hàng hiện tại
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart() {
        return ResponseEntity.ok(ApiResponse.ok(cartService.getMyCart()));
    }

    // POST /cart/items → Thêm sản phẩm vào giỏ
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @Valid @RequestBody AddToCartRequest request) {
        CartResponse cart = cartService.addToCart(request);
        return ResponseEntity.ok(ApiResponse.ok("Thêm vào giỏ hàng thành công", cart));
    }

    // PUT /cart/items/{itemId} → Cập nhật số lượng
    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật thành công",
                cartService.updateCartItem(itemId, request)));
    }

    // DELETE /cart/items/{itemId} → Xóa 1 sản phẩm khỏi giỏ
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.ok("Đã xóa khỏi giỏ hàng",
                cartService.removeCartItem(itemId)));
    }
}

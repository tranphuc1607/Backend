package com.drinkorder.controller;

import com.drinkorder.dto.common.ApiResponse;
import com.drinkorder.dto.review.ReviewRequest;
import com.drinkorder.dto.review.ReviewResponse;
import com.drinkorder.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller quản lý đánh giá sản phẩm.
 * Base URL: /api/v1/products/{productId}/reviews
 *
 * GET    /products/{productId}/reviews      → Lấy đánh giá (public)
 * POST   /products/{productId}/reviews      → Thêm đánh giá (cần đăng nhập)
 * PUT    /reviews/{reviewId}                → Cập nhật đánh giá
 * DELETE /reviews/{reviewId}               → Xóa đánh giá
 */
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Lấy tất cả đánh giá của 1 sản phẩm (public, không cần đăng nhập)
    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getProductReviews(
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.getProductReviews(productId)));
    }

    // Thêm đánh giá (cần JWT)
    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> addReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse data = reviewService.addReview(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Đánh giá thành công", data));
    }

    // Cập nhật đánh giá
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật đánh giá thành công",
                reviewService.updateReview(reviewId, request)));
    }

    // Xóa đánh giá
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.ok("Xóa đánh giá thành công", null));
    }
}

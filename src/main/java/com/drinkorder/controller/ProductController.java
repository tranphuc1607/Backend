package com.drinkorder.controller;

import com.drinkorder.dto.common.ApiResponse;
import com.drinkorder.dto.common.PageResponse;
import com.drinkorder.dto.product.ProductDetailResponse;
import com.drinkorder.dto.product.ProductRequest;
import com.drinkorder.dto.product.ProductSummaryResponse;
import com.drinkorder.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Danh sách sản phẩm: lọc categoryId, tìm keyword, phân trang.
     * GET /products?categoryId=1&keyword=tra&page=0&size=10
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductSummaryResponse>>> list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ProductSummaryResponse> data = productService.search(categoryId, keyword, page, size);
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> detail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getDetail(id)));
    }

    /** Chỉ ADMIN được thêm/sửa/ẩn sản phẩm. GET vẫn mở cho app khách. */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> create(@Valid @RequestBody ProductRequest request) {
        ProductDetailResponse created = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Tạo sản phẩm thành công", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật thành công", productService.update(id, request)));
    }

    /** Soft delete: đặt active = false */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Đã ẩn sản phẩm", null));
    }
}

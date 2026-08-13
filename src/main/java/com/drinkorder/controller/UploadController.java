package com.drinkorder.controller;

import com.drinkorder.dto.common.ApiResponse;
import com.drinkorder.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Upload ảnh cho sản phẩm/danh mục. Chỉ ADMIN dùng được.
 * Base URL: /api/v1/uploads
 */
@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final FileStorageService fileStorageService;

    /**
     * POST /uploads/images (multipart, field "file")
     * Trả về đường dẫn tương đối, ví dụ: {"url": "/uploads/abc-123.jpg"}
     * Client ghép với địa chỉ API của mình để hiển thị.
     */
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file
    ) {
        String url = fileStorageService.storeImage(file);
        return ResponseEntity.ok(
                ApiResponse.ok("Tải ảnh lên thành công", Map.of("url", url)));
    }
}

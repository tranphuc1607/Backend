package com.drinkorder.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO nhận dữ liệu khi user đánh giá sản phẩm.
 */
@Data
public class ReviewRequest {

    @NotNull(message = "Vui lòng chọn số sao")
    @Min(value = 1, message = "Đánh giá tối thiểu 1 sao")
    @Max(value = 5, message = "Đánh giá tối đa 5 sao")
    private Integer rating;

    // Comment không bắt buộc
    private String comment;
}

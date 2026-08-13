package com.drinkorder.dto.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProductRequest {

    @NotNull(message = "categoryId không được để trống")
    private Long categoryId;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 200)
    private String name;

    private String description;

    @Size(max = 500)
    private String imageUrl;

    private Boolean active = true;

    @NotEmpty(message = "Phải có ít nhất một size")
    @Valid
    private List<ProductSizeRequest> sizes;

    /** Danh sách id topping (có thể để trống). */
    private List<Long> toppingIds;
}

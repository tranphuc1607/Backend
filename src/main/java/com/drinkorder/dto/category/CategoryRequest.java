package com.drinkorder.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @Size(max = 500)
    private String imageUrl;

    private Boolean active = true;
}

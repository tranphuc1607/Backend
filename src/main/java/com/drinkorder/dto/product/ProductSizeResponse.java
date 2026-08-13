package com.drinkorder.dto.product;

import com.drinkorder.entity.ProductSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSizeResponse {

    private Long id;
    private String sizeName;
    private BigDecimal price;

    public static ProductSizeResponse fromEntity(ProductSize size) {
        return ProductSizeResponse.builder()
                .id(size.getId())
                .sizeName(size.getSizeName())
                .price(size.getPrice())
                .build();
    }
}

package com.drinkorder.dto.product;

import com.drinkorder.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Comparator;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSummaryResponse {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private String imageUrl;
    private Boolean active;
    private BigDecimal minPrice;

    public static ProductSummaryResponse fromEntity(Product product) {
        BigDecimal minPrice = product.getSizes().stream()
                .map(s -> s.getPrice())
                .min(Comparator.naturalOrder())
                .orElse(null);

        return ProductSummaryResponse.builder()
                .id(product.getId())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .active(product.getActive())
                .minPrice(minPrice)
                .build();
    }
}

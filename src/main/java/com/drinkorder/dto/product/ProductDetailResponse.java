package com.drinkorder.dto.product;

import com.drinkorder.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailResponse {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private String imageUrl;
    private Boolean active;
    private List<ProductSizeResponse> sizes;
    private List<ToppingSummaryResponse> toppings;

    public static ProductDetailResponse fromEntity(Product product) {
        return ProductDetailResponse.builder()
                .id(product.getId())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .active(product.getActive())
                .sizes(product.getSizes().stream()
                        .map(ProductSizeResponse::fromEntity)
                        .toList())
                .toppings(product.getToppings().stream()
                        .filter(t -> Boolean.TRUE.equals(t.getActive()))
                        .map(ToppingSummaryResponse::fromEntity)
                        .toList())
                .build();
    }
}

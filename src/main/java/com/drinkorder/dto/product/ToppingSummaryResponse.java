package com.drinkorder.dto.product;

import com.drinkorder.entity.Topping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToppingSummaryResponse {

    private Long id;
    private String name;
    private BigDecimal price;

    public static ToppingSummaryResponse fromEntity(Topping topping) {
        return ToppingSummaryResponse.builder()
                .id(topping.getId())
                .name(topping.getName())
                .price(topping.getPrice())
                .build();
    }
}

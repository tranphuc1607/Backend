package com.drinkorder.repository;

import com.drinkorder.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository cho item trong giỏ hàng.
 *
 * LƯU Ý QUAN TRỌNG:
 * Không dùng Spring Data method name để tìm theo toppingId = null,
 * vì Spring Data tạo "topping_id = null" thay vì "topping_id IS NULL".
 * Phải dùng @Query với JPQL để xử lý đúng cả 2 trường hợp:
 *   - toppingId != null: WHERE ci.topping.id = :toppingId
 *   - toppingId == null: WHERE ci.topping IS NULL
 */
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Tìm CartItem trùng khớp (cùng product + size + topping) trong giỏ hàng.
     * Dùng để cộng dồn số lượng thay vì tạo item mới.
     *
     * JPQL xử lý null:
     *   - Nếu toppingId != null → tìm theo topping.id = toppingId
     *   - Nếu toppingId == null → tìm cart item không có topping
     */
    @Query("SELECT ci FROM CartItem ci " +
            "WHERE ci.cart.id = :cartId " +
            "AND ci.product.id = :productId " +
            "AND ci.productSize.id = :sizeId " +
            "AND (:toppingId IS NULL AND ci.topping IS NULL " +
            "     OR :toppingId IS NOT NULL AND ci.topping.id = :toppingId)")
    Optional<CartItem> findDuplicateItem(
            @Param("cartId") Long cartId,
            @Param("productId") Long productId,
            @Param("sizeId") Long sizeId,
            @Param("toppingId") Long toppingId
    );
}

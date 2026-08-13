package com.drinkorder.repository;

import com.drinkorder.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository cho giỏ hàng.
 */
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Tìm giỏ hàng theo userId, load cả items để tránh N+1
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items ci " +
            "LEFT JOIN FETCH ci.product LEFT JOIN FETCH ci.productSize LEFT JOIN FETCH ci.topping " +
            "WHERE c.user.id = :userId")
    Optional<Cart> findByUserIdWithItems(@Param("userId") Long userId);

    // Tìm giỏ hàng đơn giản theo userId
    Optional<Cart> findByUserId(Long userId);
}

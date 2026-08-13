package com.drinkorder.repository;

import com.drinkorder.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository cho bảng product_sizes.
 */
public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {

    // Tìm size theo productId và tên size
    Optional<ProductSize> findByProductIdAndSizeName(Long productId, String sizeName);
}

package com.drinkorder.repository;

import com.drinkorder.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho địa chỉ giao hàng.
 */
public interface AddressRepository extends JpaRepository<Address, Long> {

    // Lấy tất cả địa chỉ của 1 user
    List<Address> findByUserId(Long userId);

    // Tìm địa chỉ mặc định của user
    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);

    // Đếm số địa chỉ của user
    long countByUserId(Long userId);
}

package com.drinkorder.repository;

import com.drinkorder.entity.Topping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ToppingRepository extends JpaRepository<Topping, Long> {

    List<Topping> findByIdInAndActiveTrue(List<Long> ids);
}

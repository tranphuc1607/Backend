package com.drinkorder.repository;

import com.drinkorder.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(
            value = """
                    SELECT p FROM Product p
                    JOIN FETCH p.category c
                    WHERE p.active = true
                      AND (:categoryId IS NULL OR c.id = :categoryId)
                      AND (:keyword IS NULL OR :keyword = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
                    """,
            countQuery = """
                    SELECT COUNT(p) FROM Product p
                    WHERE p.active = true
                      AND (:categoryId IS NULL OR p.category.id = :categoryId)
                      AND (:keyword IS NULL OR :keyword = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
                    """
    )
    Page<Product> searchActive(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    long countByCategory_Id(Long categoryId);

    /**
     * Chỉ join fetch MỘT collection (toppings).
     *
     * Fetch đồng thời p.sizes và p.toppings sinh tích Descartes: 3 size x 2 topping
     * = 6 dòng, và vì sizes là List nên nó nhận đủ 6 phần tử - mỗi size bị lặp
     * đúng bằng số topping. SELECT DISTINCT không cứu được: từ Hibernate 6,
     * distinct chỉ lọc entity gốc chứ không lọc phần tử collection.
     *
     * sizes đã khai báo FetchType.EAGER nên Hibernate tự nạp bằng một query riêng,
     * không trùng lặp và cũng không gây lỗi lazy.
     */
    @Query("""
            SELECT p FROM Product p
            JOIN FETCH p.category
            LEFT JOIN FETCH p.toppings
            WHERE p.id = :id
            """)
    java.util.Optional<Product> findDetailById(@Param("id") Long id);
}

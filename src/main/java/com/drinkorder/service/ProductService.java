package com.drinkorder.service;

import com.drinkorder.dto.common.PageResponse;
import com.drinkorder.dto.product.ProductDetailResponse;
import com.drinkorder.dto.product.ProductRequest;
import com.drinkorder.dto.product.ProductSizeRequest;
import com.drinkorder.dto.product.ProductSummaryResponse;
import com.drinkorder.entity.Product;
import com.drinkorder.entity.ProductSize;
import com.drinkorder.entity.Topping;
import com.drinkorder.exception.BadRequestException;
import com.drinkorder.exception.ResourceNotFoundException;
import com.drinkorder.repository.ProductRepository;
import com.drinkorder.repository.ToppingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ToppingRepository toppingRepository;

    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> search(Long categoryId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> result = productRepository.searchActive(categoryId, keyword, pageable);
        Page<ProductSummaryResponse> mapped = result.map(ProductSummaryResponse::fromEntity);
        return PageResponse.from(mapped);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getDetail(Long id) {
        Product product = productRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm id=" + id));
        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new ResourceNotFoundException("Sản phẩm không còn kinh doanh");
        }
        return ProductDetailResponse.fromEntity(product);
    }

    @Transactional
    public ProductDetailResponse create(ProductRequest request) {
        validateUniqueSizes(request.getSizes());
        Product product = Product.builder()
                .category(categoryService.findCategory(request.getCategoryId()))
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        applySizes(product, request.getSizes());
        applyToppings(product, request.getToppingIds());

        Product saved = productRepository.save(product);
        return ProductDetailResponse.fromEntity(
                productRepository.findDetailById(saved.getId()).orElse(saved)
        );
    }

    @Transactional
    public ProductDetailResponse update(Long id, ProductRequest request) {
        validateUniqueSizes(request.getSizes());
        Product product = findProduct(id);
        product.setCategory(categoryService.findCategory(request.getCategoryId()));
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        mergeSizes(product, request.getSizes());
        product.getToppings().clear();
        applyToppings(product, request.getToppingIds());

        Product saved = productRepository.save(product);
        return ProductDetailResponse.fromEntity(
                productRepository.findDetailById(saved.getId()).orElse(saved)
        );
    }

    @Transactional
    public void delete(Long id) {
        Product product = findProduct(id);
        product.setActive(false);
        productRepository.save(product);
    }

    public Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm id=" + id));
    }

    private void applySizes(Product product, List<ProductSizeRequest> sizeRequests) {
        for (ProductSizeRequest sr : sizeRequests) {
            ProductSize size = ProductSize.builder()
                    .product(product)
                    .sizeName(sr.getSizeName().trim().toUpperCase())
                    .price(sr.getPrice())
                    .build();
            product.getSizes().add(size);
        }
    }

    /**
     * Cập nhật bộ size theo kiểu hợp nhất, thay vì xoá sạch rồi thêm lại.
     *
     * Hai lý do phải làm vậy:
     * 1. clear() + add() cùng một tên size trong một flush sẽ vi phạm unique
     *    (product_id, size_name), vì Hibernate chạy INSERT trước khi xoá orphan.
     *    Hệ quả là thao tác thường gặp nhất — sửa giá size cũ — luôn lỗi 500.
     * 2. CartItem và OrderItem tham chiếu ProductSize theo id. Giữ lại bản ghi cũ
     *    thì giỏ hàng của khách không bị trỏ vào size đã biến mất.
     */
    private void mergeSizes(Product product, List<ProductSizeRequest> sizeRequests) {
        Map<String, ProductSizeRequest> wanted = new LinkedHashMap<>();
        for (ProductSizeRequest sr : sizeRequests) {
            wanted.put(sr.getSizeName().trim().toUpperCase(), sr);
        }

        // Bỏ những size không còn trong yêu cầu
        product.getSizes().removeIf(existing -> !wanted.containsKey(existing.getSizeName()));

        // Size giữ lại: cập nhật giá, đồng thời loại khỏi danh sách cần thêm mới
        for (ProductSize existing : product.getSizes()) {
            ProductSizeRequest sr = wanted.remove(existing.getSizeName());
            if (sr != null) {
                existing.setPrice(sr.getPrice());
            }
        }

        // Phần còn lại là size thật sự mới
        for (Map.Entry<String, ProductSizeRequest> entry : wanted.entrySet()) {
            product.getSizes().add(ProductSize.builder()
                    .product(product)
                    .sizeName(entry.getKey())
                    .price(entry.getValue().getPrice())
                    .build());
        }
    }

    private void applyToppings(Product product, List<Long> toppingIds) {
        if (toppingIds == null || toppingIds.isEmpty()) {
            return;
        }
        List<Topping> toppings = toppingRepository.findByIdInAndActiveTrue(toppingIds);
        if (toppings.size() != new HashSet<>(toppingIds).size()) {
            throw new BadRequestException("Một hoặc nhiều topping không hợp lệ");
        }
        product.getToppings().addAll(new HashSet<>(toppings));
    }

    private void validateUniqueSizes(List<ProductSizeRequest> sizes) {
        // Qua API thì @NotEmpty ở ProductRequest đã chặn trước, nên nhánh này không chạm tới.
        // Giữ lại để nếu sau này có chỗ gọi service trực tiếp thì báo 400 thay vì NPE 500.
        if (sizes == null || sizes.isEmpty()) {
            throw new BadRequestException("Sản phẩm phải có ít nhất một size");
        }
        Set<String> names = new HashSet<>();
        for (ProductSizeRequest s : sizes) {
            String key = s.getSizeName().trim().toUpperCase();
            if (!names.add(key)) {
                throw new BadRequestException("Trùng tên size: " + key);
            }
        }
    }
}

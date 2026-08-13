package com.drinkorder.service;

import com.drinkorder.dto.category.CategoryRequest;
import com.drinkorder.dto.category.CategoryResponse;
import com.drinkorder.entity.Category;
import com.drinkorder.exception.BadRequestException;
import com.drinkorder.exception.ResourceNotFoundException;
import com.drinkorder.repository.CategoryRepository;
import com.drinkorder.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        return CategoryResponse.fromEntity(findCategory(id));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findCategory(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }
        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        Category category = findCategory(id);
        if (productRepository.countByCategory_Id(id) > 0) {
            throw new BadRequestException("Không thể xóa danh mục đang có sản phẩm. Hãy ẩn (active=false) thay vì xóa.");
        }
        categoryRepository.delete(category);
    }

    public Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục id=" + id));
    }
}

package com.drinkorder.service;

import com.drinkorder.dto.review.ReviewRequest;
import com.drinkorder.dto.review.ReviewResponse;
import com.drinkorder.entity.Product;
import com.drinkorder.entity.Review;
import com.drinkorder.entity.User;
import com.drinkorder.exception.BadRequestException;
import com.drinkorder.exception.ResourceNotFoundException;
import com.drinkorder.repository.ProductRepository;
import com.drinkorder.repository.ReviewRepository;
import com.drinkorder.repository.UserRepository;
import com.drinkorder.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý logic đánh giá sản phẩm.
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // Lấy tất cả đánh giá của 1 sản phẩm (public)
    // readOnly = true: Review.user là LAZY, cần session còn mở lúc map DTO.
    @Transactional(readOnly = true)
    public List<ReviewResponse> getProductReviews(Long productId) {
        // Kiểm tra product tồn tại
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm");
        }
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(ReviewResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // User đánh giá sản phẩm
    @Transactional
    public ReviewResponse addReview(Long productId, ReviewRequest request) {
        User user = getCurrentUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

        // Kiểm tra user đã đánh giá sản phẩm này chưa
        if (reviewRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            throw new BadRequestException("Bạn đã đánh giá sản phẩm này rồi");
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return ReviewResponse.fromEntity(reviewRepository.save(review));
    }

    // User cập nhật đánh giá của mình
    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request) {
        User user = getCurrentUser();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bạn không có quyền chỉnh sửa đánh giá này");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        return ReviewResponse.fromEntity(reviewRepository.save(review));
    }

    // User xóa đánh giá của mình
    @Transactional
    public void deleteReview(Long reviewId) {
        User user = getCurrentUser();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bạn không có quyền xóa đánh giá này");
        }

        reviewRepository.delete(review);
    }

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }
}

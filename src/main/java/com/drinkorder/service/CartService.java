package com.drinkorder.service;

import com.drinkorder.dto.cart.AddToCartRequest;
import com.drinkorder.dto.cart.CartResponse;
import com.drinkorder.dto.cart.UpdateCartItemRequest;
import com.drinkorder.entity.Cart;
import com.drinkorder.entity.CartItem;
import com.drinkorder.entity.Product;
import com.drinkorder.entity.ProductSize;
import com.drinkorder.entity.Topping;
import com.drinkorder.entity.User;
import com.drinkorder.exception.BadRequestException;
import com.drinkorder.exception.ResourceNotFoundException;
import com.drinkorder.repository.CartItemRepository;
import com.drinkorder.repository.CartRepository;
import com.drinkorder.repository.ProductRepository;
import com.drinkorder.repository.ProductSizeRepository;
import com.drinkorder.repository.ToppingRepository;
import com.drinkorder.repository.UserRepository;
import com.drinkorder.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service xử lý logic giỏ hàng.
 *
 * Luồng thêm sản phẩm vào giỏ:
 * 1. Lấy user đang đăng nhập
 * 2. Tìm hoặc tạo mới giỏ hàng cho user
 * 3. Tìm product, size, topping từ database
 * 4. Kiểm tra nếu đã có item trùng (cùng product + size + topping) → cộng dồn số lượng
 * 5. Nếu chưa có → tạo CartItem mới
 * 6. Lưu và trả về giỏ hàng cập nhật
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;
    private final ToppingRepository toppingRepository;
    private final UserRepository userRepository;

    // ==================== PUBLIC METHODS ====================

    /**
     * Lấy giỏ hàng hiện tại của user đang đăng nhập.
     * Nếu user chưa có giỏ hàng, tạo mới.
     */
    @Transactional
    public CartResponse getMyCart() {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        return CartResponse.fromEntity(cart);
    }

    /**
     * Thêm sản phẩm vào giỏ hàng.
     * Nếu đã tồn tại item trùng (product + size + topping) → cộng dồn quantity.
     * Nếu chưa có → tạo CartItem mới.
     */
    @Transactional
    public CartResponse addToCart(AddToCartRequest request) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        // 1. Tìm product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

        // Kiểm tra sản phẩm có đang bán không
        if (!product.getActive()) {
            throw new BadRequestException("Sản phẩm này hiện không có sẵn");
        }

        // 2. Tìm size
        ProductSize size = productSizeRepository.findById(request.getProductSizeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy size"));

        // Kiểm tra size có thuộc product này không
        if (!size.getProduct().getId().equals(product.getId())) {
            throw new BadRequestException("Size không thuộc sản phẩm này");
        }

        // 3. Tìm topping (có thể null)
        Topping topping = null;
        if (request.getToppingId() != null) {
            topping = toppingRepository.findById(request.getToppingId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy topping"));
        }

        // 4. Tính giá đơn vị = giá size + giá topping (nếu có)
        BigDecimal unitPrice = size.getPrice();
        if (topping != null) {
            unitPrice = unitPrice.add(topping.getPrice());
        }

        // 5. Kiểm tra đã có item trùng chưa (dùng @Query để xử lý null toppingId)
        Long toppingId = topping != null ? topping.getId() : null;
        Optional<CartItem> existingItem = cartItemRepository.findDuplicateItem(
                cart.getId(), product.getId(), size.getId(), toppingId);

        if (existingItem.isPresent()) {
            // Cộng dồn số lượng
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            // Tạo CartItem mới
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .productSize(size)
                    .topping(topping)
                    .quantity(request.getQuantity())
                    .unitPrice(unitPrice)
                    .build();
            // Dùng cascade - thêm vào list của cart rồi save cart
            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);

        // Reload để lấy dữ liệu mới nhất (tránh dùng cache cũ)
        Cart updatedCart = cartRepository.findByUserIdWithItems(user.getId())
                .orElseThrow();
        return CartResponse.fromEntity(updatedCart);
    }

    /**
     * Cập nhật số lượng của 1 item trong giỏ hàng.
     * Chỉ được cập nhật item thuộc giỏ hàng của mình.
     */
    @Transactional
    public CartResponse updateCartItem(Long cartItemId, UpdateCartItemRequest request) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        // Tìm item trong giỏ hàng của user (không cho sửa item của người khác)
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng"));

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        Cart updated = cartRepository.findByUserIdWithItems(user.getId()).orElseThrow();
        return CartResponse.fromEntity(updated);
    }

    /**
     * Xóa 1 item khỏi giỏ hàng.
     * Chỉ được xóa item thuộc giỏ hàng của mình.
     */
    @Transactional
    public CartResponse removeCartItem(Long cartItemId) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng"));

        // orphanRemoval = true trong Cart nên xóa khỏi list là đủ
        cart.getItems().remove(item);
        cartRepository.save(cart);

        Cart updated = cartRepository.findByUserIdWithItems(user.getId()).orElseThrow();
        return CartResponse.fromEntity(updated);
    }

    /**
     * Xóa toàn bộ sản phẩm trong giỏ hàng (gọi sau khi đặt hàng thành công).
     * Phương thức này được OrderService gọi nội bộ.
     */
    @Transactional
    public void clearCart(Long userId) {
        cartRepository.findByUserIdWithItems(userId).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    // ==================== PRIVATE HELPERS ====================

    /**
     * Lấy giỏ hàng của user. Nếu chưa có thì tạo mới.
     */
    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserIdWithItems(user.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Lấy thông tin user đang đăng nhập từ JWT token.
     */
    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }
}

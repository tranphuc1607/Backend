package com.drinkorder.service;

import com.drinkorder.dto.order.CreateOrderRequest;
import com.drinkorder.dto.order.OrderResponse;
import com.drinkorder.dto.order.UpdateOrderStatusRequest;
import com.drinkorder.entity.Address;
import com.drinkorder.entity.Cart;
import com.drinkorder.entity.CartItem;
import com.drinkorder.entity.Order;
import com.drinkorder.entity.OrderItem;
import com.drinkorder.entity.OrderStatus;
import com.drinkorder.entity.User;
import com.drinkorder.exception.BadRequestException;
import com.drinkorder.exception.ResourceNotFoundException;
import com.drinkorder.repository.AddressRepository;
import com.drinkorder.repository.CartRepository;
import com.drinkorder.repository.OrderRepository;
import com.drinkorder.repository.UserRepository;
import com.drinkorder.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý logic đặt hàng.
 * Luồng đặt hàng:
 * 1. Lấy giỏ hàng hiện tại của user
 * 2. Kiểm tra giỏ không rỗng
 * 3. Tìm địa chỉ giao hàng user chọn
 * 4. Tạo Order và danh sách OrderItem từ giỏ hàng
 * 5. Tính tổng tiền
 * 6. Lưu vào database
 * 7. Xóa giỏ hàng
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    // User xem lịch sử đơn hàng của mình
    // readOnly = true: giữ session mở khi map DTO, nếu không Order.user (LAZY) sẽ
    // ném LazyInitializationException vì open-in-view đang tắt.
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders() {
        User user = getCurrentUser();
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // User xem chi tiết 1 đơn hàng (chỉ xem được đơn của mình)
    @Transactional(readOnly = true)
    public OrderResponse getMyOrderById(Long orderId) {
        User user = getCurrentUser();
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));
        return OrderResponse.fromEntity(order);
    }

    // Đặt hàng
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User user = getCurrentUser();

        // Lấy giỏ hàng
        Cart cart = cartRepository.findByUserIdWithItems(user.getId())
                .orElseThrow(() -> new BadRequestException("Giỏ hàng không tồn tại"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Giỏ hàng đang trống, vui lòng thêm sản phẩm");
        }

        // Lấy địa chỉ giao hàng
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ giao hàng"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Địa chỉ không hợp lệ");
        }

        // Tạo địa chỉ full dưới dạng string
        String fullAddress = buildFullAddress(address);

        // Tạo danh sách OrderItem từ CartItem
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            BigDecimal subtotal = cartItem.getUnitPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .product(cartItem.getProduct())
                    .productName(cartItem.getProduct().getName())
                    .sizeName(cartItem.getProductSize().getSizeName())
                    .toppingName(cartItem.getTopping() != null ? cartItem.getTopping().getName() : null)
                    .unitPrice(cartItem.getUnitPrice())
                    .quantity(cartItem.getQuantity())
                    .subtotal(subtotal)
                    .build();
            orderItems.add(orderItem);
        }

        // Tạo đơn hàng
        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .deliveryAddress(fullAddress)
                .recipientName(address.getRecipientName())
                .recipientPhone(address.getRecipientPhone())
                .note(request.getNote())
                .items(orderItems)
                .build();

        // Gán quan hệ order cho từng item
        orderItems.forEach(item -> item.setOrder(order));

        Order saved = orderRepository.save(order);

        // Xóa giỏ hàng sau khi đặt thành công
        cartService.clearCart(user.getId());

        return OrderResponse.fromEntity(saved);
    }

    // User hủy đơn hàng (chỉ được hủy khi đang PENDING)
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        User user = getCurrentUser();
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException(
                    "Không thể hủy đơn hàng ở trạng thái: " + order.getStatus()
            );
        }

        order.setStatus(OrderStatus.CANCELLED);
        return OrderResponse.fromEntity(orderRepository.save(order));
    }

    // ======== ADMIN ========

    // Admin lấy tất cả đơn hàng (có phân trang)
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(OrderResponse::fromEntity);
    }

    // Admin lấy đơn theo trạng thái
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
                .map(OrderResponse::fromEntity);
    }

    /**
     * Admin tra cứu đơn theo trạng thái và/hoặc thời gian.
     *
     * date       : lọc đúng một ngày, ưu tiên hơn fromDate/toDate.
     * fromDate/toDate: lọc một khoảng, bao gồm cả hai đầu; để trống một bên
     *                  thì thành khoảng mở về phía đó.
     *
     * Lọc thời gian phải làm ở server: nếu lọc phía app thì chỉ lọc được
     * trong trang hiện tại, đơn cùng kỳ nằm ở trang sau sẽ bị bỏ sót.
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> searchOrders(
            OrderStatus status,
            LocalDate date,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable) {

        LocalDateTime from;
        LocalDateTime to;

        if (date != null) {
            from = date.atStartOfDay();
            to = date.plusDays(1).atStartOfDay();
        } else {
            // Người dùng chọn ngược đầu (từ 20/8 đến 10/8) thì tự đảo lại
            // thay vì trả về danh sách rỗng khó hiểu.
            LocalDate lo = fromDate;
            LocalDate hi = toDate;
            if (lo != null && hi != null && lo.isAfter(hi)) {
                final LocalDate tmp = lo;
                lo = hi;
                hi = tmp;
            }
            from = lo != null ? lo.atStartOfDay() : null;
            // +1 ngày vì mốc trên là nửa mở: bao trọn cả ngày cuối
            to = hi != null ? hi.plusDays(1).atStartOfDay() : null;
        }

        return orderRepository.searchForAdmin(status, from, to, pageable)
                .map(OrderResponse::fromEntity);
    }

    // Admin xem chi tiết 1 đơn hàng
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));
        return OrderResponse.fromEntity(order);
    }

    // Admin cập nhật trạng thái đơn hàng
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        // Không cho phép thay đổi đơn đã hủy hoặc đã giao
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Đơn hàng đã hủy, không thể cập nhật");
        }

        order.setStatus(request.getStatus());
        return OrderResponse.fromEntity(orderRepository.save(order));
    }

    // ---- Helper methods ----

    private String buildFullAddress(Address address) {
        StringBuilder sb = new StringBuilder(address.getStreet());
        if (address.getWard() != null) sb.append(", ").append(address.getWard());
        if (address.getDistrict() != null) sb.append(", ").append(address.getDistrict());
        if (address.getCity() != null) sb.append(", ").append(address.getCity());
        return sb.toString();
    }

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }
}

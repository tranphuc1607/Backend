package com.drinkorder.service;

import com.drinkorder.dto.address.AddressRequest;
import com.drinkorder.dto.address.AddressResponse;
import com.drinkorder.entity.Address;
import com.drinkorder.entity.User;
import com.drinkorder.exception.BadRequestException;
import com.drinkorder.exception.ResourceNotFoundException;
import com.drinkorder.repository.AddressRepository;
import com.drinkorder.repository.UserRepository;
import com.drinkorder.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý logic địa chỉ giao hàng.
 */
@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    // Lấy danh sách địa chỉ của user đang đăng nhập
    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses() {
        User user = getCurrentUser();
        return addressRepository.findByUserId(user.getId())
                .stream()
                .map(AddressResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Thêm địa chỉ mới
    @Transactional
    public AddressResponse addAddress(AddressRequest request) {
        User user = getCurrentUser();

        // Nếu đây là địa chỉ đầu tiên hoặc user muốn đặt làm default
        // thì xóa default cũ trước
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearDefaultAddress(user.getId());
        }

        // Nếu chưa có địa chỉ nào, tự động đặt làm default
        boolean isFirst = addressRepository.countByUserId(user.getId()) == 0;

        Address address = Address.builder()
                .user(user)
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .street(request.getStreet())
                .ward(request.getWard())
                .district(request.getDistrict())
                .city(request.getCity())
                .isDefault(isFirst || Boolean.TRUE.equals(request.getIsDefault()))
                .build();

        return AddressResponse.fromEntity(addressRepository.save(address));
    }

    // Cập nhật địa chỉ
    @Transactional
    public AddressResponse updateAddress(Long id, AddressRequest request) {
        User user = getCurrentUser();
        Address address = getAddressAndVerifyOwner(id, user.getId());

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearDefaultAddress(user.getId());
        }

        address.setRecipientName(request.getRecipientName());
        address.setRecipientPhone(request.getRecipientPhone());
        address.setStreet(request.getStreet());
        address.setWard(request.getWard());
        address.setDistrict(request.getDistrict());
        address.setCity(request.getCity());
        address.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()));

        return AddressResponse.fromEntity(addressRepository.save(address));
    }

    // Xóa địa chỉ
    @Transactional
    public void deleteAddress(Long id) {
        User user = getCurrentUser();
        Address address = getAddressAndVerifyOwner(id, user.getId());
        addressRepository.delete(address);
    }

    // Đặt làm địa chỉ mặc định
    @Transactional
    public AddressResponse setDefault(Long id) {
        User user = getCurrentUser();
        Address address = getAddressAndVerifyOwner(id, user.getId());

        clearDefaultAddress(user.getId());
        address.setIsDefault(true);
        return AddressResponse.fromEntity(addressRepository.save(address));
    }

    // ---- Helper methods ----

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    private Address getAddressAndVerifyOwner(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));
        if (!address.getUser().getId().equals(userId)) {
            throw new BadRequestException("Bạn không có quyền thao tác địa chỉ này");
        }
        return address;
    }

    private void clearDefaultAddress(Long userId) {
        addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(addr -> {
                    addr.setIsDefault(false);
                    addressRepository.save(addr);
                });
    }
}

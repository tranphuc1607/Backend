package com.drinkorder.dto.address;

import com.drinkorder.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO trả về thông tin địa chỉ giao hàng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {

    private Long id;
    private String recipientName;
    private String recipientPhone;
    private String street;
    private String ward;
    private String district;
    private String city;
    private Boolean isDefault;

    // Địa chỉ đầy đủ hiển thị gộp lại
    private String fullAddress;

    public static AddressResponse fromEntity(Address address) {
        String full = address.getStreet()
                + (address.getWard() != null ? ", " + address.getWard() : "")
                + (address.getDistrict() != null ? ", " + address.getDistrict() : "")
                + (address.getCity() != null ? ", " + address.getCity() : "");

        return AddressResponse.builder()
                .id(address.getId())
                .recipientName(address.getRecipientName())
                .recipientPhone(address.getRecipientPhone())
                .street(address.getStreet())
                .ward(address.getWard())
                .district(address.getDistrict())
                .city(address.getCity())
                .isDefault(address.getIsDefault())
                .fullAddress(full)
                .build();
    }
}

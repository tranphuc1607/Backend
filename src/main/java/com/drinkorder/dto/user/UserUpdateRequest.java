package com.drinkorder.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @Size(max = 150)
    private String fullName;

    @Size(max = 20)
    private String phone;
}

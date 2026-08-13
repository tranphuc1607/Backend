package com.drinkorder.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

// Bean được đăng ký qua @EnableConfigurationProperties ở DrinkOrderApplication.
// Không thêm @Configuration: sẽ bị component-scan tạo thêm bean thứ 2 cùng type.
@ConfigurationProperties(prefix = "app.admin")
@Getter
@Setter
public class AdminSeedProperties {

    private String email;
    private String password;
    private String fullName;
}

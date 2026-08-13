package com.drinkorder.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

// Bean được đăng ký qua @EnableConfigurationProperties ở DrinkOrderApplication.
// Không thêm @Configuration: sẽ bị component-scan tạo thêm bean thứ 2 cùng type.
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {

    private String secret;
    private long expirationMs;
}

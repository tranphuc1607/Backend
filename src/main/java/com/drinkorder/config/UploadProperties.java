package com.drinkorder.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

// Bean được đăng ký qua @EnableConfigurationProperties ở DrinkOrderApplication.
// Không thêm @Configuration: sẽ bị component-scan tạo thêm bean thứ 2 cùng type.
@ConfigurationProperties(prefix = "app.upload")
@Getter
@Setter
public class UploadProperties {

    /** Thư mục trên đĩa nơi lưu file đã upload. */
    private String dir = "uploads";

    /** Tiền tố URL công khai để đọc lại file, ví dụ /uploads. */
    private String urlPrefix = "/uploads";
}

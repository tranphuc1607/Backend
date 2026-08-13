package com.drinkorder.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Cho phép đọc lại ảnh đã upload qua HTTP.
 *
 * Ảnh nằm ngoài jar (trong thư mục/volume) nên phải khai báo resource handler
 * trỏ tới đường dẫn file thật, Spring không tự phục vụ giúp.
 * Địa chỉ đầy đủ là: /api/v1/uploads/<tên file> (đã tính context-path).
 */
@Configuration
@RequiredArgsConstructor
public class StaticResourceConfig implements WebMvcConfigurer {

    private final UploadProperties properties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path dir = Paths.get(properties.getDir()).toAbsolutePath().normalize();
        registry.addResourceHandler(properties.getUrlPrefix() + "/**")
                .addResourceLocations(dir.toUri().toString())
                .setCachePeriod(3600);
    }
}

package com.drinkorder.security;

import com.drinkorder.dto.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Trả 403 khi user ĐÃ đăng nhập nhưng không đủ quyền.
 *
 * Không có handler này, Spring Security đẩy luôn sang JwtAuthenticationEntryPoint
 * và trả 403 dưới dạng 401 "Chưa đăng nhập" — app Flutter hiểu nhầm là hết phiên
 * rồi tự đăng xuất người dùng, trong khi thực tế chỉ là thiếu quyền.
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(),
                ApiResponse.fail("Bạn không có quyền thực hiện thao tác này"));
    }
}

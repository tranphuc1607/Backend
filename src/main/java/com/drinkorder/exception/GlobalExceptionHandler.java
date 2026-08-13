package com.drinkorder.exception;

import com.drinkorder.dto.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.stream.Collectors;

/**
 * Xử lý tập trung tất cả các Exception từ Controller.
 * Trả về JSON dạng ApiResponse thống nhất thay vì HTML error page mặc định.
 *
 * Các loại lỗi xử lý:
 * - ResourceNotFoundException (404): không tìm thấy tài nguyên
 * - BadRequestException (400): yêu cầu không hợp lệ
 * - MethodArgumentNotValidException (400): validation thất bại (@Valid)
 * - HttpMessageNotReadableException (400): JSON không đúng định dạng (vd: enum sai giá trị)
 * - BadCredentialsException (401): sai email hoặc mật khẩu
 * - DisabledException (403): tài khoản bị vô hiệu hoá
 * - AccessDeniedException (403): không có quyền
 * - Exception (500): lỗi hệ thống không xác định
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - Không tìm thấy tài nguyên
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(ex.getMessage()));
    }

    // 400 - Yêu cầu không hợp lệ (business logic)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(ex.getMessage()));
    }

    // 400 - Validation thất bại (@Valid annotation)
    // Gom tất cả lỗi validation thành 1 message
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(message));
    }

    // 400 - JSON không đọc được hoặc enum value sai
    // Ví dụ: paymentMethod = "INVALID_VALUE" → lỗi này
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(HttpMessageNotReadableException ex) {
        String message = "Dữ liệu gửi lên không hợp lệ. Kiểm tra lại định dạng JSON.";
        // Cố gắng lấy thông tin chi tiết hơn nếu có
        if (ex.getMessage() != null && ex.getMessage().contains("not one of the values accepted")) {
            message = "Giá trị enum không hợp lệ. " +
                    "Các giá trị thanh toán chấp nhận: COD, MOMO. " +
                    "Các trạng thái đơn: PENDING, CONFIRMED, PREPARING, DELIVERING, DELIVERED, CANCELLED.";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(message));
    }

    // 400 - Tham số trên URL sai kiểu: ngày sai định dạng, enum không hợp lệ,
    // id không phải số... Trước đây rơi xuống handler chung và trả 500.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Class<?> type = ex.getRequiredType();
        String hint = "";
        if (type != null && type.isEnum()) {
            hint = " Giá trị hợp lệ: " + String.join(", ",
                    java.util.Arrays.stream(type.getEnumConstants())
                            .map(Object::toString).toList()) + ".";
        } else if (type == java.time.LocalDate.class) {
            hint = " Định dạng ngày phải là yyyy-MM-dd.";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail("Tham số \"" + ex.getName() + "\" không hợp lệ." + hint));
    }

    // 415 - Gọi endpoint upload mà không gửi dạng multipart/form-data
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ApiResponse.fail("Định dạng gửi lên không được hỗ trợ. Ảnh phải gửi dạng multipart/form-data."));
    }

    // 400 - Upload nhưng thiếu phần "file" trong multipart
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingPart(MissingServletRequestPartException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail("Thiếu file trong yêu cầu. Gửi kèm trường \"" + ex.getRequestPartName() + "\"."));
    }

    // 400 - Thiếu tham số bắt buộc trên query string
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail("Thiếu tham số bắt buộc: " + ex.getParameterName()));
    }

    // 413 - Ảnh vượt quá giới hạn dung lượng
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleTooLarge(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.fail("Ảnh quá lớn. Dung lượng tối đa là 5MB."));
    }

    // 401 - Sai email hoặc mật khẩu khi login
    // Không nói rõ sai email hay sai mật khẩu, tránh cho biết email nào đang tồn tại.
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail("Email hoặc mật khẩu không đúng"));
    }

    // 403 - Tài khoản bị vô hiệu hoá (User.enabled = false)
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<Void>> handleDisabled(DisabledException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.fail("Tài khoản đã bị vô hiệu hoá"));
    }

    // 403 - Không có quyền truy cập
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.fail("Bạn không có quyền thực hiện thao tác này"));
    }

    // 500 - Lỗi hệ thống không xác định
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        // Log lỗi (trong thực tế nên dùng Logger)
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("Lỗi hệ thống: " + ex.getMessage()));
    }
}

package com.drinkorder.service;

import com.drinkorder.config.UploadProperties;
import com.drinkorder.exception.BadRequestException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

/**
 * Lưu ảnh upload xuống đĩa và trả về đường dẫn công khai.
 *
 * Nguyên tắc bảo mật:
 * - Không dùng tên file client gửi lên (tránh path traversal kiểu ../../).
 *   Tên file mới sinh bằng UUID, đuôi lấy từ loại ảnh đã xác thực.
 * - Không tin Content-Type client khai báo. Đọc mấy byte đầu (magic number)
 *   để xác định thật sự là JPEG/PNG/WebP hay không - nếu không sẽ có người
 *   đổi tên shell.jsp thành .png rồi upload.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final UploadProperties properties;

    private Path root;

    /** Đuôi file tương ứng với từng loại ảnh được chấp nhận. */
    private static final Map<String, String> EXTENSIONS = Map.of(
            "jpeg", ".jpg",
            "png", ".png",
            "webp", ".webp"
    );

    @PostConstruct
    void init() {
        root = Paths.get(properties.getDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
            log.info("Thư mục lưu ảnh: {}", root);
        } catch (IOException e) {
            throw new IllegalStateException("Không tạo được thư mục lưu ảnh: " + root, e);
        }
    }

    /**
     * Lưu ảnh, trả về đường dẫn công khai dạng "/uploads/<tên>.jpg".
     * Đường dẫn này là tương đối; client tự ghép với địa chỉ server của nó
     * (emulator dùng 10.0.2.2, web dùng localhost) nên không hard-code host.
     */
    public String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Chưa chọn file ảnh");
        }

        String kind = detectImageKind(file);
        if (kind == null) {
            throw new BadRequestException(
                    "File không phải ảnh hợp lệ. Chỉ nhận JPG, PNG hoặc WebP.");
        }

        String filename = UUID.randomUUID() + EXTENSIONS.get(kind);
        Path target = root.resolve(filename).normalize();

        // Chốt chặn cuối: file phải nằm trong thư mục upload
        if (!target.startsWith(root)) {
            throw new BadRequestException("Tên file không hợp lệ");
        }

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Lưu ảnh thất bại", e);
            throw new IllegalStateException("Không lưu được ảnh", e);
        }

        return properties.getUrlPrefix() + "/" + filename;
    }

    /**
     * Nhận diện loại ảnh bằng magic number ở đầu file.
     * Trả về "jpeg" / "png" / "webp", hoặc null nếu không phải ảnh hỗ trợ.
     */
    private String detectImageKind(MultipartFile file) {
        byte[] head = new byte[12];
        try (InputStream in = file.getInputStream()) {
            int read = in.read(head);
            if (read < 12) return null;
        } catch (IOException e) {
            return null;
        }

        // JPEG: FF D8 FF
        if ((head[0] & 0xFF) == 0xFF && (head[1] & 0xFF) == 0xD8 && (head[2] & 0xFF) == 0xFF) {
            return "jpeg";
        }
        // PNG: 89 50 4E 47 0D 0A 1A 0A
        if ((head[0] & 0xFF) == 0x89 && head[1] == 'P' && head[2] == 'N' && head[3] == 'G'
                && (head[4] & 0xFF) == 0x0D && (head[5] & 0xFF) == 0x0A
                && (head[6] & 0xFF) == 0x1A && (head[7] & 0xFF) == 0x0A) {
            return "png";
        }
        // WebP: "RIFF" .... "WEBP"
        if (head[0] == 'R' && head[1] == 'I' && head[2] == 'F' && head[3] == 'F'
                && head[8] == 'W' && head[9] == 'E' && head[10] == 'B' && head[11] == 'P') {
            return "webp";
        }
        return null;
    }
}

# Drink Order API

Backend REST API cho ứng dụng đặt đồ uống. Spring Boot 3.4.1 · Java 21 · MySQL 8 · JWT.

App Flutter nằm ở repo riêng: **drink-order-app** — chạy backend này trước rồi mới chạy app.

---

## Chạy (chỉ cần Docker)

```bash
git clone <url-repo-nay>
cd drink-order-api
docker compose up -d --build
```

Xong. Lần đầu mất vài phút để build, các lần sau vài giây.

- API: **http://localhost:8081/api/v1**
- MySQL: **localhost:3307** (user `root`, mật khẩu `123456`)

Compose dựng 2 container: `db` (MySQL 8.4) và `api`. Container `api` chỉ khởi động sau khi
MySQL healthy nên không gặp lỗi "connection refused" lúc boot. Bảng do Hibernate tự tạo
(`ddl-auto: update`), không cần chạy SQL tay.

### Chạy xong có sẵn những gì

Lần đầu chạy trên database rỗng, app tự tạo:

| | |
|---|---|
| Danh mục | 6 (Trà sữa, Cà phê, Trà trái cây, Nước ép, Đá xay, Soda) |
| Sản phẩm | 24 món, đủ size và topping, **kèm ảnh thật** |
| Topping | 8 |
| Tài khoản admin | `admin@drinkorder.com` / `123456` |

Nghĩa là clone về là dùng được ngay, không phải nhập tay hay import file SQL nào.
Dữ liệu do `DataSeeder.java` và `AdminSeeder.java` tạo; cả hai chỉ chạy khi database
còn rỗng nên khởi động lại nhiều lần cũng không sinh trùng.

Muốn đổi tài khoản admin thì đặt biến môi trường `APP_ADMIN_EMAIL` / `APP_ADMIN_PASSWORD`
**trước lần chạy đầu tiên**. Nếu admin đã tồn tại, seeder bỏ qua và mật khẩu cũ giữ nguyên.

### Ảnh sản phẩm

Có hai nguồn ảnh tách biệt:

| Loại | Nơi lưu | Đường dẫn |
|---|---|---|
| Ảnh thực đơn mẫu | `src/main/resources/static/seed-images/` (đi theo repo, nằm trong jar) | `/seed-images/...` |
| Ảnh admin tự tải lên | Docker volume `upload-data` | `/uploads/...` |

Cả hai đều xem được không cần đăng nhập. Upload thì phải là ADMIN.

### Các lệnh hay dùng

```bash
docker compose logs -f api     # xem log API
docker compose down            # dừng, GIỮ dữ liệu và ảnh đã upload
docker compose down -v         # dừng và XOÁ SẠCH database lẫn ảnh đã upload
docker compose up -d --build   # build lại sau khi sửa code
```

`docker compose up -d --build` không làm mất dữ liệu — chỉ `down -v` mới xoá.

### Đổi cấu hình

Mặc định chạy được ngay, không cần `.env`. Muốn đổi thì `cp .env.example .env` rồi sửa.
Đáng chú ý là `MYSQL_HOST_PORT` — mặc định **3307** để không đụng MySQL cài sẵn trên máy
(thường chiếm 3306).

---

## Chạy trực tiếp không dùng Docker

Cần **JDK 21**, **Maven 3.9+**, **MySQL 8** chạy sẵn ở `localhost:3306` với `root`/`123456`.

```bash
mvn spring-boot:run
```

Cấu hình ở `src/main/resources/application.yml`. Docker không sửa file này mà ghi đè bằng
biến môi trường (`SPRING_DATASOURCE_URL`...), nên hai cách chạy không đá nhau.

Ảnh thực đơn mẫu nằm trong classpath nên cách này cũng có ảnh đầy đủ. Riêng ảnh upload sẽ
ghi vào thư mục `uploads/` cạnh nơi chạy lệnh.

---

## API

Base URL: `http://localhost:8081/api/v1`. Response luôn bọc trong:

```json
{ "success": true, "message": "OK", "data": { } }
```

| Nhóm | Method | Endpoint | Quyền |
|---|---|---|---|
| Xác thực | POST | `/auth/register`, `/auth/login`, `/auth/logout` | công khai |
| Danh mục | GET | `/categories`, `/categories/{id}` | công khai |
| | POST/PUT/DELETE | `/categories`, `/categories/{id}` | ADMIN |
| Sản phẩm | GET | `/products?keyword=&categoryId=&page=&size=`, `/products/{id}` | công khai |
| | POST/PUT/DELETE | `/products`, `/products/{id}` | ADMIN |
| Đánh giá | GET | `/products/{id}/reviews` | công khai |
| | POST/PUT/DELETE | `/products/{id}/reviews`, `/reviews/{id}` | đã đăng nhập |
| Giỏ hàng | GET/POST/PUT/DELETE | `/cart`, `/cart/items`, `/cart/items/{id}` | đã đăng nhập |
| Địa chỉ | GET/POST/PUT/DELETE | `/addresses`, `/addresses/{id}`, `/addresses/{id}/default` | đã đăng nhập |
| Đơn hàng | GET/POST/PUT | `/orders/my`, `/orders`, `/orders/{id}/cancel` | đã đăng nhập |
| Quản trị đơn | GET | `/orders/admin?status=&date=&from=&to=&page=&size=` | ADMIN |
| | PUT | `/orders/admin/{id}/status` | ADMIN |
| Thống kê | GET | `/admin/dashboard`, `/admin/users` | ADMIN |
| | DELETE | `/admin/users/{id}` | ADMIN |
| Tải ảnh | POST | `/uploads/images` (multipart, field `file`) | ADMIN |
| Ảnh tĩnh | GET | `/uploads/**`, `/seed-images/**` | công khai |

Lọc đơn theo thời gian: `date` cho một ngày, `from`/`to` cho một khoảng (bao gồm cả hai
đầu). Truyền `date` thì `from`/`to` bị bỏ qua.

---

## Cấu trúc

```
src/main/java/com/drinkorder/
├── DrinkOrderApplication.java
├── config/          # Security, CORS, seeder, upload, JPA auditing
├── controller/      # REST endpoints
├── service/         # Nghiệp vụ
├── repository/      # Spring Data JPA
├── entity/          # Map bảng MySQL
├── dto/             # Request/Response JSON
├── security/        # JWT filter, entry point, access denied handler
└── exception/       # Xử lý lỗi tập trung

src/main/resources/static/seed-images/   # 24 ảnh thực đơn mẫu
docs/sql/schema.sql                      # DDL tham khảo, không bắt buộc chạy
```

## Ghi chú

- `DELETE /products/{id}` là **ẩn** sản phẩm (`active=false`), không xoá thật.
  `DELETE /admin/users/{id}` cũng là vô hiệu hoá, không xoá.
- Xoá danh mục sẽ bị từ chối nếu còn sản phẩm — hãy ẩn thay vì xoá.
- Token JWT sống **24 giờ** (`app.jwt.expiration-ms`). Hết hạn thì app tự đưa về màn đăng nhập.
- **JWT secret trong `application.yml` là giá trị mặc định cho môi trường học tập.**
  Nếu deploy thật, bắt buộc đặt lại qua biến `JWT_SECRET`.
- Ảnh upload được kiểm tra bằng magic number chứ không tin `Content-Type` client gửi;
  chỉ nhận JPG, PNG, WebP và tối đa 5MB.

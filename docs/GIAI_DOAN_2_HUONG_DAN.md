# Giai đoạn 2 — Spring Boot: Category & Product

## Đã hoàn thành

- Project Maven `drink-order-api` (Java 21, Spring Boot 3.4)
- Cấu hình MySQL trong `application.yml`
- Entity: `Category`, `Product`, `ProductSize`, `Topping`
- Repository, DTO, Service, Controller
- REST API Category + Product (CRUD, tìm kiếm, lọc, phân trang)
- `ApiResponse` + `GlobalExceptionHandler`
- `DataSeeder` tạo dữ liệu demo khi DB trống

## Cấu hình MySQL

Sửa `drink-order-api/src/main/resources/application.yml`:

```yaml
username: root
password: <mat-khau-cua-ban>
```

## Chạy backend

Trong IntelliJ: mở `drink-order-api` → Run `DrinkOrderApplication`.

Hoặc terminal (cần Maven trong PATH):

```powershell
cd E:\Nam_4\ProjectBTL\drink-order-api
mvn spring-boot:run
```

URL gốc: **http://localhost:8080/api/v1**

## Danh sách file chính

| File | Vai trò |
|------|---------|
| `DrinkOrderApplication.java` | Khởi động Spring Boot |
| `entity/BaseEntity.java` | id, createdAt, updatedAt |
| `entity/Category.java` | Bảng categories |
| `entity/Product.java` | Bảng products + sizes + toppings |
| `entity/ProductSize.java` | Giá theo S/M/L |
| `entity/Topping.java` | Topping |
| `repository/*Repository.java` | Truy vấn JPA |
| `service/CategoryService.java` | Nghiệp vụ danh mục |
| `service/ProductService.java` | Nghiệp vụ sản phẩm |
| `controller/CategoryController.java` | `/categories` |
| `controller/ProductController.java` | `/products` |
| `dto/common/ApiResponse.java` | JSON thống nhất |
| `exception/GlobalExceptionHandler.java` | Bắt lỗi 400/404/500 |

## Postman — ví dụ

**GET** `http://localhost:8080/api/v1/categories`

**GET** `http://localhost:8080/api/v1/products?keyword=tra&page=0&size=10`

**GET** `http://localhost:8080/api/v1/products/1`

**POST** `http://localhost:8080/api/v1/categories`

```json
{
  "name": "Sinh tố",
  "description": "Sinh tố trái cây",
  "active": true
}
```

**POST** `http://localhost:8080/api/v1/products`

```json
{
  "categoryId": 1,
  "name": "Trà đào",
  "description": "Trà đào cam sả",
  "sizes": [
    { "sizeName": "M", "price": 32000 },
    { "sizeName": "L", "price": 38000 }
  ],
  "toppingIds": [1]
}
```

## Giai đoạn 3

Đăng ký, đăng nhập, JWT, phân quyền USER/ADMIN (khóa POST/PUT/DELETE admin).

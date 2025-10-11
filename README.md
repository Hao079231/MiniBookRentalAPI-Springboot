# Mini Book Rental — API

Tài liệu này hướng dẫn cách chạy và sử dụng API của dự án Mini Book Rental (backend Spring Boot).

## Tổng quan
Mini Book Rental quản lý sách, danh mục, bạn đọc, nhân viên và các giao dịch mượn/trả. Ứng dụng sử dụng JWT để xác thực và phân quyền theo vai trò.

## Yêu cầu trước khi chạy
- Java 17 (hoặc phiên bản tương thích được khai báo trong `pom.xml`)
- Maven

Chạy ứng dụng từ thư mục gốc dự án:

```powershell
# Windows (PowerShell)
./mvnw.cmd spring-boot:run

# Hoặc dùng Maven đã cài
mvn spring-boot:run
```

Ứng dụng mặc định chạy trên cổng 8080 (xem `src/main/resources/application.properties`).

## Cấu hình
Các file cấu hình:
- `src/main/resources/application.properties`
- `src/main/resources/application-dev.properties`

## Xác thực (JWT)
Để gọi các API bảo mật, trước tiên cần lấy token bằng endpoint xác thực.

- Endpoint: `POST /api/token`
- Body yêu cầu: JSON theo `AuthenticationForm` (xem `src/main/java/com/ute/rental/form/AuthenticationForm.java`)
- Response: token trả về trong trường `data` của `ApiMessageDto`

Sau khi có token, gửi header Authorization cho các request cần xác thực:

```
Authorization: Bearer <token>
```

Ví dụ lấy token (PowerShell/curl):
```powershell
curl -X POST http://localhost:8080/api/token -H "Content-Type: application/json" -d '{"grantType":"admin","username":"admin","password":"Password123!"}'
```

## Định dạng phản hồi chung
Tất cả API trả về đối tượng `ApiMessageDto` (xem `src/main/java/com/ute/rental/dto/ApiMessageDto.java`):

```json
{
  "result": true,
  "code": "200",
  "data": { /* payload */ },
  "message": "Thông điệp (nếu có)"
}
```

## Tóm tắt các endpoint chính
Các controller nằm trong `src/main/java/com/ute/rental/controller`. Nhiều endpoint được bảo vệ bằng phân quyền theo role (xem annotation `@PreAuthorize` trong controller).

Authentication
- POST /api/token — `AuthenticationController`

Book (`BookController`)
- POST   /v1/book/create
- GET    /v1/book/list
- GET    /v1/book/get/{id}
- PUT    /v1/book/update
- DELETE /v1/book/delete/{id}

Category (`CategoryController`)
- POST /v1/category/create
- GET  /v1/category/list
- PUT  /v1/category/update

Reader (`ReaderController`)
- POST /v1/reader/create
- GET  /v1/reader/list
- PUT  /v1/reader/update
- DELETE /v1/reader/delete/{id}
- PUT  /v1/reader/unblock

Rental Transaction (`RentalTransactionController`)
- POST /v1/rental-transaction/create
- GET  /v1/rental-transaction/list
- GET  /v1/rental-transaction/get/{id}
- PUT  /v1/rental-transaction/update
- PUT  /v1/rental-transaction/complete

Các controller khác: `StaffController`, `GroupController`, `PermissionController` — xem trực tiếp file controller để biết chi tiết.

## DTO và Form chính
Các lớp request/response chính:
- `src/main/java/com/ute/rental/form/` — các lớp form dùng làm body request (ví dụ `CreateBookForm`, `CreateRentalTransaction`)
- `src/main/java/com/ute/rental/dto/`  — các DTO phản hồi (ví dụ `BookDto`, `RentalTransactionDto`)

## Xử lý lỗi
Mã lỗi được định nghĩa trong `ErrorCode` và ngoại lệ được xử lý tập trung trong `GlobalExceptionHandler`.

## Ghi chú phát triển
- Entry point ứng dụng: `MiniBookRentalApplication` (`src/main/java/com/ute/rental/MiniBookRentalApplication.java`).
- Cấu hình bảo mật & JWT: `SecurityConfig`, `ResourceConfig`, `CustomJwtDecoder` (thư mục `config`).
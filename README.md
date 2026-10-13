# [Bài tập] Giao tiếp bất đồng bộ với Redis Pub/Sub

Dự án triển khai mô hình **Redis Pub/Sub** phục vụ thông báo bất đồng bộ theo cơ chế Message Broker nhẹ nhàng (Lesson 03 - Redis Pub/Sub) trong kiến trúc Microservice.

---

## 1. Mục tiêu
- **Kiến thức**: Hiểu cơ chế Message Broker nhẹ nhàng của Redis Pub/Sub theo mô hình Fire-and-forget.
- **Kỹ năng**: 
  - Triển khai **Publisher** gửi message lên Redis channel.
  - Triển khai **Subscriber** (`MessageListener`) lắng nghe channel và xử lý thông tin in ra Console.
  - Sử dụng `ObjectMapper` để serialize/deserialize đối tượng Java sang JSON và ngược lại.

---

## 2. Mô tả chi tiết & Yêu cầu kỹ thuật
- **Bối cảnh**: Khi một hiệu thuốc nhập thêm hàng, hệ thống cần thông báo ngay lập tức cho tất cả các màn hình Dashboard của quản lý chi nhánh đang mở.
- **Yêu cầu kỹ thuật**:
  - **Publisher**: Khi nhập hàng thành công (hoặc thông qua API), gửi một message vào channel `pharmacy-alerts` với nội dung JSON:
    ```json
    {
      "type": "IMPORT",
      "message": "Đã nhập 100 hộp Panadol"
    }
    ```
  - **Subscriber**: Service lắng nghe channel `pharmacy-alerts` và in nội dung ra Console dưới dạng Dashboard Alert:
    ```text
    === THÔNG BÁO DASHBOARD QUẢN LÝ ===
    Type : IMPORT
    Nội dung: Đã nhập 100 hộp Panadol
    =================================
    ```
  - **Fire-and-forget**: Message được truyền đi ngay lập tức đến tất cả các service đang lắng nghe mà không cần lưu trữ lại trên Broker.

---

## 3. Cấu trúc thư mục

```
Ss11-Ex03/
├── docker-compose.yml              # Khởi chạy Redis Server
├── pom.xml                         # Spring Boot 3.4.3, Spring Data Redis, Jackson, Lombok
├── README.md                       # Tài liệu hướng dẫn
└── src/
    ├── main/
    │   ├── java/com/example/pharmacy/
    │   │   ├── PharmacyApplication.java
    │   │   ├── config/
    │   │   │   └── RedisPubSubConfig.java       # Cấu hình Redis Pub/Sub container & adapter
    │   │   ├── consumer/
    │   │   │   └── PharmacyAlertSubscriber.java # Lắng nghe channel và in log
    │   │   ├── controller/
    │   │   │   └── PharmacyAlertController.java # REST API kích hoạt gửi alert
    │   │   ├── dto/
    │   │   │   └── AlertMessage.java            # DTO định dạng JSON message
    │   │   └── publisher/
    │   │       └── PharmacyAlertPublisher.java  # Service gửi message JSON vào Redis channel
    │   └── resources/
    │       └── application.yml
    └── test/
        └── java/com/example/pharmacy/
            └── PharmacyAlertTests.java          # Unit test kiểm tra Producer & Consumer
```

---

## 4. Hướng dẫn cài đặt & Khởi chạy

### Bước 1: Khởi động Redis
Sử dụng docker-compose:
```bash
docker-compose up -d
```
Hoặc Redis server chạy tại `localhost:6379`.

### Bước 2: Build & Chạy ứng dụng
```bash
mvn clean package -DskipTests
mvn spring-boot:run
```

---

## 5. Kiểm thử API

### Gửi thông báo nhập hàng đến channel `pharmacy-alerts`:
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/v1/alerts`
- **Headers**: `Content-Type: application/json`
- **Body**:
```json
{
  "topicListen": "pharmacy-alerts",
  "type": "IMPORT",
  "message": "Đã nhập 100 hộp Panadol"
}
```
- **Response**: `200 OK`
```text
Đã gửi thông báo đến kênh : pharmacy-alerts
```

### Kết quả hiển thị trên Console Subscriber:
```text
INFO --- [pharmacy-service] : === THÔNG BÁO DASHBOARD QUẢN LÝ ===
INFO --- [pharmacy-service] : Type : IMPORT
INFO --- [pharmacy-service] : Nội dung: Đã nhập 100 hộp Panadol
INFO --- [pharmacy-service] : =================================
```

# Bài 4: Khai Báo GitHub Secrets Và Cấu Hình Đăng Nhập Container Registry (GHCR)

## 🎯 1. Mục Tiêu
- Khởi tạo và quản lý biến bí mật an toàn (**Repository Secret**) trên GitHub để lưu trữ Personal Access Token (PAT).
- Cấu hình bước xác thực đăng nhập tự động vào **GitHub Container Registry (`ghcr.io`)** trong luồng GitHub Actions thông qua action chính thức `docker/login-action@v3`.
- Đảm bảo an toàn bảo mật: Tuyệt đối không hardcode thông tin nhạy cảm vào mã nguồn công khai.

---

## 📁 2. Cấu Trúc Thư Mục
```text
bai4/
├── .github/
│   └── workflows/
│       └── ci.yml               # Workflow có bước xác thực login vào ghcr.io
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── src/
│   ├── main/java/com/example/demo/
│   │   └── DemoApplication.java
│   └── test/java/com/example/demo/
│       └── DemoApplicationTests.java
├── .gitignore
├── build.gradle
├── gradlew                      # Executable script (chmod 755)
├── gradlew.bat
├── settings.gradle
└── README.md                    # Tài liệu hướng dẫn cấu hình & kiểm tra
```

---

## 🔐 3. Hướng Dẫn Cấu Hình GitHub Secrets (`REGISTRY_TOKEN`)

### Bước 1: Khởi tạo Personal Access Token (PAT) trên GitHub
1. Truy cập: [GitHub Developer Settings - Personal access tokens (classic)](https://github.com/settings/tokens).
2. Nhấp chọn **Generate new token** -> **Generate new token (classic)**.
3. Đặt tên (Note), ví dụ: `GHCR_CI_TOKEN`.
4. Chọn thời hạn (Expiration), ví dụ: `30 days` hoặc `No expiration`.
5. Tích chọn các quyền (Scopes) cần thiết:
   - ✅ `write:packages` (Tự động chọn kèm `read:packages` để có quyền đẩy và tải container image).
   - ✅ `delete:packages` (Tùy chọn).
6. Nhấp **Generate token** và sao chép chuỗi mã token được sinh ra (ví dụ: `ghp_xxxxxxxxxxxx`).

---

### Bước 2: Thêm Secret vào Repository
1. Mở repository dự án trên GitHub: [re-Deloys-session11-lesson4](https://github.com/re-java-wed-application-phamcongthanh/re-Deloys-session11-lesson4).
2. Vào mục **Settings** -> danh mục bên trái chọn **Secrets and variables** -> **Actions**.
3. Tại mục **Repository secrets**, nhấp nút **New repository secret**.
4. Điền thông tin:
   - **Name**: `REGISTRY_TOKEN`
   - **Secret**: Dán chuỗi Personal Access Token vừa tạo ở Bước 1.
5. Nhấp **Add secret** để lưu lại.

---

## ⚙️ 4. Chi Tiết Kịch Bản Workflow (`.github/workflows/ci.yml`)

```yaml
name: Java Spring Boot CI & GHCR Login

on:
  push:
    branches: [ "main" ]

jobs:
  build-and-login:
    name: Run Tests, Build & Login GHCR
    runs-on: ubuntu-latest

    permissions:
      contents: read
      packages: write

    steps:
      - name: Checkout mã nguồn
        uses: actions/checkout@v5

      - name: Thiết lập môi trường Java JDK 17
        uses: actions/setup-java@v5
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: 'gradle'

      - name: Cấp quyền cho Gradle Wrapper
        run: chmod +x gradlew

      - name: Thực thi JUnit Test với Gradle
        run: ./gradlew test

      - name: Đóng gói tệp JAR với Gradle
        run: ./gradlew bootJar

      - name: Đăng nhập vào GitHub Container Registry
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.REGISTRY_TOKEN }}
```

### 💡 Phân Tích Kỹ Thuật Bước Đăng Nhập:
- `docker/login-action@v3`: Action chính thức của Docker giúp xác thực Docker CLI với các Container Registry (Docker Hub, AWS ECR, GCP GCR, GHCR).
- `registry: ghcr.io`: Chỉ định registry mục tiêu là GitHub Container Registry.
- `username: ${{ github.actor }}`: Tự động lấy tên tài khoản GitHub của người kích hoạt commit/workflow.
- `password: ${{ secrets.REGISTRY_TOKEN }}`: Lấy giá trị token đã mã hóa từ Repository Secrets. GitHub Actions sẽ tự động mask (ẩn thành `***`) trong toàn bộ console log để ngăn ngừa lộ token.

---

## ✅ 5. Xác Nhận Kết Quả Trên GitHub Actions

1. Đẩy mã nguồn lên nhánh `main`:
   ```bash
   git add .
   git commit -m "feat: complete session 11 exercise 4 with GHCR login configuration"
   git push origin main
   ```
2. Mở tab **Actions** trên GitHub Repository.
3. Chọn workflow run mới nhất và mở chi tiết bước **"Đăng nhập vào GitHub Container Registry"**.
4. Xác nhận kết quả đầu ra hiển thị thông báo thành công:
   ```text
   Logging into ghcr.io...
   Login Succeeded!
   ```
   > ✅ **Xác nhận**: Trạng thái hiển thị tích xanh (Success), xác thực GHCR hoàn tất sẵn sàng cho công đoạn build và push Docker Image ở các pipeline CD tiếp theo.

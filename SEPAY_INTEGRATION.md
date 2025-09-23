# 🚀 Hướng dẫn tích hợp SePay vào hệ thống

## ✅ Đã hoàn thành

### 1. **Cấu hình SePay**

- ✅ Thêm cấu hình SePay vào `application.properties`
- ✅ Tạo `SePayConfig.java` để quản lý cấu hình
- ✅ Tạo `SePayService.java` để xử lý thanh toán
- ✅ Tạo `SePayController.java` để xử lý callback

### 2. **Tích hợp Email Service**

- ✅ Cập nhật `KhachHangService.java` với SePay QR code
- ✅ Thay thế MoMo QR bằng SePay QR động
- ✅ Cập nhật email template với thông tin tài khoản MBBank

### 3. **Controller xử lý thanh toán**

- ✅ Tạo `SePayController.java` để xử lý callback
- ✅ Tạo `SePayTestController.java` để test
- ✅ Tạo `SePayQRGenerator.java` để tạo QR code mẫu

## 🧪 Cách test

### 1. **Test tạo QR code SePay**

```bash
# Tạo QR code mẫu
GET http://localhost:8080/test/sepay/generate-sample

# Tạo QR code tùy chỉnh
GET http://localhost:8080/test/sepay/generate-custom?amount=500000&orderId=TEST123

# Test tạo link thanh toán
GET http://localhost:8080/test/sepay/test-payment-url?amount=100000&orderId=TEST456

# Test URL ảnh QR từ SePay API
GET http://localhost:8080/test/sepay/test-qr-url?amount=100000&orderId=TEST123

# Test QR code với tiền cọc (50% tổng tiền)
GET http://localhost:8080/test/sepay/test-deposit-qr?totalAmount=200000&orderId=BOOK123

# Test nhúng QR code trực tiếp vào HTML
GET http://localhost:8080/sepay-embed/test?amount=100000&orderId=TEST123

# API trả về HTML với QR code nhúng
GET http://localhost:8080/sepay-embed/qr-html?amount=100000&orderId=TEST123&description=DatPhong_TEST123

# Debug SePay URL (kiểm tra lỗi)
GET http://localhost:8080/debug/sepay/test-url?amount=50000&orderId=BOOK1758620982318

# Test URL SePay trực tiếp
GET http://localhost:8080/debug/sepay/test-direct-url

# Tạo QR code fallback (ZXing)
GET http://localhost:8080/debug/sepay/fallback-qr?amount=50000&orderId=BOOK1758620982318

# Test nút QR SePay (giải pháp mới)
GET http://localhost:8080/sepay-button/test?amount=50000&orderId=BOOK1758620982318

# API lấy URL QR cho nút
GET http://localhost:8080/sepay-button/qr-url?amount=50000&orderId=BOOK1758620982318

# Nhân viên kiểm tra giao dịch
GET http://localhost:8080/nhan-vien/transaction-check

# Mở Google Sheets SePay
GET http://localhost:8080/nhan-vien/transaction-check/open-sheets?bookingCode=BOOK1758620982318
```

### 2. **Test API SePay**

```bash
# Tạo QR code động (download từ SePay API)
GET http://localhost:8080/sepay/qr?amount=100000&orderId=BOOK123

# Test QR code
GET http://localhost:8080/sepay/test-qr

# Lấy URL ảnh QR từ SePay API
GET http://localhost:8080/sepay/qr-url?amount=100000&orderId=BOOK123

# Lấy thông tin tài khoản ngân hàng
GET http://localhost:8080/sepay/bank-info
```

### 3. **Test callback**

```bash
# URL return sau khi thanh toán
GET http://localhost:8080/sepay/return?orderId=BOOK123&amount=100000&status=success

# Webhook notification
POST http://localhost:8080/sepay/notify
Content-Type: application/json
{
  "orderId": "BOOK123",
  "amount": "100000",
  "status": "success",
  "signature": "abc123"
}
```

## 📧 Cách sử dụng trong code

### **Tạo URL QR code SePay để nhúng:**

```java
@Autowired
private SePayService sePayService;

// Tạo URL QR code cho booking
String qrImageUrl = sePayService.getSePayQRImageUrl(
    booking.getTienDatCoc(), // Sử dụng tiền cọc
    booking.getMaDatPhong(),
    "DatPhong_" + booking.getMaDatPhong()
);

// Nhúng vào HTML/Email
String html = "<img src='" + qrImageUrl + "' width='250' height='250' alt='QR Code SePay'/>";
```

### **Nhúng vào Email:**

```java
// Thay vì sử dụng attachment
// helper.addInline("qr_sepay", qrImage, "image/png");

// Sử dụng URL trực tiếp
String emailHtml = String.format(
    "<p>Vui lòng thanh toán tiền cọc:</p>" +
    "<img src='%s' width='250' height='250' alt='QR Code SePay'/>" +
    "<p>Số tiền cọc: %,.0f VNĐ</p>",
    qrImageUrl, booking.getTienDatCoc()
);
```

### **Nhúng vào Website:**

```html
<!-- Nhúng trực tiếp vào HTML -->
<img
  src="https://qr.sepay.vn/img?acc=1392005882005&bank=MBBank&amount=100000&des=DatPhong_BOOK123&template=compact"
  width="250"
  height="250"
  alt="QR Code SePay"
/>

<!-- Hoặc sử dụng API -->
<img
  src="/sepay/qr-url?amount=100000&orderId=BOOK123"
  width="250"
  height="250"
  alt="QR Code SePay"
/>
```

## 🎨 Tính năng SePay

### **QR Code từ API thực tế:**

- ✅ **Sử dụng API SePay thực tế**: [https://qr.sepay.vn](https://qr.sepay.vn/img?acc=SO_TAI_KHOAN&bank=NGAN_HANG&amount=SO_TIEN&des=NOI_DUNG&template=TEMPLATE&download=DOWNLOAD)
- ✅ Thông tin tài khoản MBBank: 1392005882005
- ✅ **Số tiền cọc (50% tổng tiền phòng)** thay vì tổng tiền
- ✅ Nội dung chuyển khoản: DatPhong\_[MãBooking]
- ✅ Template compact cho email
- ✅ **Nhúng trực tiếp vào HTML/Email** thay vì download
- ✅ Fallback về ZXing nếu API không hoạt động

### **Email template mới:**

- ✅ Header đẹp mắt với logo Panacea Hotel
- ✅ Thông tin đặt phòng chi tiết
- ✅ QR code SePay động với **tiền cọc**
- ✅ Thông tin tài khoản ngân hàng rõ ràng
- ✅ Hiển thị rõ số tiền cọc cần thanh toán
- ✅ Hướng dẫn thanh toán từng bước
- ✅ Lưu ý về tiền cọc và số tiền còn lại

## 🔧 Cấu hình

### **application.properties:**

```properties
# Cấu hình SePay
sepay.bank-account=1392005882005
sepay.bank-name=MBBank
sepay.account-holder=Panacea Hotel
sepay.api-url=https://api.sepay.vn
sepay.qr-code-size=250
sepay.qr-code-format=PNG
sepay.return-url=https://b813094b7291.ngrok-free.app/sepay/return
sepay.notify-url=https://b813094b7291.ngrok-free.app/sepay/notify
```

## 🚀 Lợi ích so với MoMo QR

| MoMo QR cũ                      | SePay mới                               |
| ------------------------------- | --------------------------------------- |
| ❌ Ảnh tĩnh, không tích hợp     | ✅ QR code động với thông tin thực      |
| ❌ Không có thông tin tài khoản | ✅ Hiển thị đầy đủ thông tin MBBank     |
| ❌ Không có callback            | ✅ Có callback và webhook               |
| ❌ Không thể tùy chỉnh          | ✅ Có thể tùy chỉnh số tiền và nội dung |

## 🎯 Kết luận

Việc thay thế MoMo QR bằng SePay là **HOÀN TOÀN HỢP LÝ** và mang lại nhiều lợi ích:

- ✅ **Tự động hóa hoàn toàn** - QR code được tạo động với thông tin chính xác
- ✅ **Tích hợp thực tế** - Có thể nhận thanh toán thật từ các ví điện tử
- ✅ **Thông tin rõ ràng** - Khách hàng biết chính xác tài khoản và số tiền
- ✅ **Callback handling** - Có thể xử lý kết quả thanh toán tự động
- ✅ **Dễ bảo trì** - Code được tổ chức tốt và có thể mở rộng

## 🔧 Troubleshooting

### **QR Code bị lỗi trong email:**

1. **Kiểm tra URL SePay:**

   ```bash
   GET http://localhost:8080/debug/sepay/test-url?amount=50000&orderId=BOOK1758620982318
   ```

2. **Test URL trực tiếp:**

   ```bash
   GET http://localhost:8080/debug/sepay/test-direct-url
   ```

3. **Sử dụng fallback QR:**
   ```bash
   GET http://localhost:8080/debug/sepay/fallback-qr?amount=50000&orderId=BOOK1758620982318
   ```

### **Các lỗi thường gặp:**

- ❌ **SePay API down**: URL không trả về ảnh
- ❌ **Invalid parameters**: Tham số không hợp lệ
- ❌ **Network issues**: Vấn đề kết nối mạng

### **Giải pháp:**

1. **Nút QR Code (Khuyến nghị)** - Thay vì nhúng ảnh, tạo nút click:

   ```html
   <a
     href="https://qr.sepay.vn/img?acc=1392005882005&bank=MBBank&amount=50000&des=DatPhong_BOOK123&template=compact"
     target="_blank"
     style="..."
   >
     📱 Quét QR Code SePay
   </a>
   ```

2. **Hệ thống tự động fallback** về ZXing nếu SePay API lỗi
3. **QR code được tạo bằng Base64** và nhúng trực tiếp vào email
4. **Placeholder image** nếu cả hai phương pháp đều lỗi

### **Lợi ích của nút QR:**

- ✅ **Không bị lỗi ảnh** trong email
- ✅ **Hoạt động trên mọi email client** (Gmail, Outlook, Apple Mail...)
- ✅ **Mở trong tab mới** - không làm mất email
- ✅ **Có thể test URL** trước khi gửi
- ✅ **Fallback tự động** nếu SePay API lỗi
- ✅ **UX tốt hơn** - khách hàng biết phải làm gì

## 👥 **Tính Năng Cho Nhân Viên**

### **Kiểm Tra Giao Dịch SePay**

Nhân viên có thể sử dụng tính năng kiểm tra giao dịch để:

1. **Tìm kiếm giao dịch** theo mã booking
2. **Truy cập Google Sheets** SePay để xem chi tiết
3. **Tạo QR code** để kiểm tra nhanh
4. **Xác nhận thanh toán** từ khách hàng

### **Cách Sử Dụng:**

1. **Truy cập trang kiểm tra:**

   ```
   http://localhost:8080/nhan-vien/transaction-check
   ```

2. **Nhập mã booking** cần kiểm tra

3. **Nhấn "Kiểm Tra Giao Dịch"** để tìm trong database

4. **Nhấn "Mở Google Sheets"** để xem chi tiết giao dịch

5. **Tạo QR code** để kiểm tra nhanh (tùy chọn)

### **Google Sheets SePay:**

- **URL:** [https://docs.google.com/spreadsheets/d/1CG0W13E-wvMZEkPXp-k4y2cFlpGW2l8Q0dFnoYsUZzg/edit?gid=0#gid=0](https://docs.google.com/spreadsheets/d/1CG0W13E-wvMZEkPXp-k4y2cFlpGW2l8Q0dFnoYsUZzg/edit?gid=0#gid=0)
- **Chứa:** Thông tin giao dịch SePay chi tiết
- **Cập nhật:** Real-time từ hệ thống
- **Tìm kiếm:** Ctrl+F để tìm theo mã booking

### **Lợi Ích:**

- ✅ **Kiểm tra nhanh** giao dịch SePay
- ✅ **Truy cập dễ dàng** Google Sheets
- ✅ **Tìm kiếm hiệu quả** theo mã booking
- ✅ **Xác nhận thanh toán** chính xác
- ✅ **Lưu trữ dữ liệu** an toàn trên Google Sheets

## 📞 Thông tin liên hệ

- **Tài khoản ngân hàng:** 1392005882005
- **Ngân hàng:** MBBank
- **Chủ tài khoản:** Panacea Hotel
- **Nội dung chuyển khoản:** DatPhong\_[MãBooking]

## 📧 **Ví dụ Email với Nút QR**

### **Email mới (với nút QR):**

```html
<p>
  Vui lòng thanh toán <b>tiền cọc</b> qua SePay bằng cách nhấn nút QR dưới đây:
</p>
<div style="text-align: center; margin: 20px 0;">
  <a
    href="https://qr.sepay.vn/img?acc=1392005882005&bank=MBBank&amount=50000&des=DatPhong_BOOK123&template=compact"
    target="_blank"
    style="display: inline-block; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); 
   color: white; padding: 20px 40px; text-decoration: none; border-radius: 50px; 
   font-weight: bold; font-size: 18px; box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3); 
   transition: all 0.3s ease; border: none; cursor: pointer;"
  >
    📱 Quét QR Code SePay</a
  >
</div>
<p><b>Tài khoản ngân hàng:</b> 1392005882005 - MBBank</p>
<p><b>Số tiền cọc cần thanh toán:</b> 50,000 VNĐ</p>
<p><b>Nội dung chuyển khoản: </b>DatPhong_BOOK123</p>
<p>
  <b>Lưu ý:</b> Đây là tiền cọc (50% tổng tiền phòng). Số tiền còn lại sẽ thanh
  toán khi nhận phòng.
</p>
<p>
  <b>Hướng dẫn:</b> Nhấn nút trên để mở QR code SePay, sau đó quét bằng app ngân
  hàng để thanh toán.
</p>
```

### **So sánh với phương pháp cũ:**

**❌ Phương pháp cũ (nhúng ảnh):**

```html
<img
  src="https://qr.sepay.vn/img?..."
  width="250"
  height="250"
  alt="QR Code SePay"
/>
```

- Có thể bị lỗi ảnh trong email
- Không hoạt động trên một số email client
- Khó debug khi có lỗi

**✅ Phương pháp mới (nút click):**

```html
<a href="https://qr.sepay.vn/img?..." target="_blank" style="...">
  📱 Quét QR Code SePay
</a>
```

- Luôn hoạt động trên mọi email client
- Mở trong tab mới, không làm mất email
- UX tốt hơn, khách hàng biết phải làm gì
- Dễ debug và test

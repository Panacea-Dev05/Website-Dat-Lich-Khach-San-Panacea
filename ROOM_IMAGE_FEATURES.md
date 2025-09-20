# Tính năng quản lý hình ảnh hạng phòng

## ✅ Đã hoàn thành

### 1. Backend (Java Spring Boot)

#### **AdminRoomService.java**

- ✅ Thêm logic load `imageUrls` trong `convertRoomTypeToDTO()`
- ✅ Thêm phương thức `listRoomImagesByRoomType()` - lấy hình ảnh theo hạng phòng
- ✅ Thêm phương thức `addRoomTypeImage()` - thêm hình ảnh mới
- ✅ Thêm phương thức `deleteRoomTypeImage()` - xóa hình ảnh
- ✅ Thêm phương thức `updateImageOrder()` - cập nhật thứ tự hiển thị
- ✅ Thêm phương thức `setMainImage()` - đặt hình ảnh chính

#### **AdminRoomController.java**

- ✅ `GET /admin/rooms/room-types/{id}/images` - Lấy danh sách hình ảnh
- ✅ `POST /admin/rooms/room-types/{id}/images` - Upload hình ảnh
- ✅ `DELETE /admin/rooms/room-types/{roomTypeId}/images/{imageId}` - Xóa hình ảnh
- ✅ `PUT /admin/rooms/room-types/{roomTypeId}/images/{imageId}/set-main` - Đặt hình chính
- ✅ `PUT /admin/rooms/room-types/images/{imageId}/order` - Cập nhật thứ tự

### 2. Frontend (HTML/JavaScript)

#### **QuanLyHangPhong.html**

- ✅ Cập nhật form submission để upload hình ảnh sau khi tạo hạng phòng
- ✅ Thêm hàm `uploadImages()` - gửi file lên server
- ✅ Thêm hàm `deleteRoomTypeImage()` - xóa hình ảnh từ giao diện
- ✅ Cải thiện hiển thị hình ảnh trong chi tiết hạng phòng
- ✅ Thêm nút xóa cho từng hình ảnh

### 3. Database & File System

#### **Entity RoomImages**

- ✅ Đã có sẵn cấu trúc database hoàn chỉnh
- ✅ Hỗ trợ quan hệ với `RoomType` và `Room`
- ✅ Có các trường: `urlHinhAnh`, `tenHinhAnh`, `moTa`, `thuTuHienThi`, `laHinhChinh`, `trangThai`

#### **File Storage**

- ✅ Tạo thư mục `src/main/resources/static/images/roomtypes/`
- ✅ Lưu file với tên unique: `roomtype_{id}_{timestamp}_{index}.{ext}`
- ✅ URL truy cập: `/images/roomtypes/{filename}`

## 🚀 Cách sử dụng

### 1. Thêm hạng phòng với hình ảnh

1. Mở trang "Quản lý hạng phòng"
2. Click "Thêm hạng phòng"
3. Điền thông tin cơ bản (Tab 1)
4. Chuyển sang Tab 2 "Hình ảnh, Mô tả"
5. Kéo thả hoặc click để chọn hình ảnh (tối đa 6 ảnh)
6. Click "Lưu" - hệ thống sẽ tự động upload hình ảnh

### 2. Xem hình ảnh hạng phòng

1. Click vào dòng hạng phòng trong bảng
2. Hình ảnh sẽ hiển thị trong phần chi tiết
3. Có thể xóa từng hình ảnh bằng nút "×" màu đỏ

### 3. Quản lý hình ảnh qua API

#### Upload hình ảnh:

```javascript
const formData = new FormData();
formData.append("files", file1);
formData.append("files", file2);

fetch("/admin/rooms/room-types/1/images", {
  method: "POST",
  body: formData,
});
```

#### Xóa hình ảnh:

```javascript
fetch("/admin/rooms/room-types/1/images/123", {
  method: "DELETE",
});
```

#### Đặt hình chính:

```javascript
fetch("/admin/rooms/room-types/1/images/123/set-main", {
  method: "PUT",
});
```

## 🔧 Cấu hình

### 1. Thư mục lưu trữ

- **Path**: `src/main/resources/static/images/roomtypes/`
- **URL**: `/images/roomtypes/`
- **Format**: `roomtype_{roomTypeId}_{timestamp}_{index}.{extension}`

### 2. Giới hạn

- **Số lượng**: Tối đa 6 hình ảnh mỗi hạng phòng
- **Kích thước**: Tối đa 5MB mỗi file
- **Định dạng**: JPG, PNG, GIF

### 3. Sắp xếp

- Hình ảnh được sắp xếp theo `thuTuHienThi` (thứ tự hiển thị)
- Nếu cùng thứ tự, sắp xếp theo ID

## 🐛 Xử lý lỗi

### 1. Upload thất bại

- Kiểm tra kích thước file (< 5MB)
- Kiểm tra định dạng file (chỉ JPG, PNG, GIF)
- Kiểm tra quyền ghi thư mục

### 2. Hiển thị không đúng

- Kiểm tra đường dẫn file có tồn tại
- Kiểm tra cấu hình static resources trong Spring Boot
- Kiểm tra trạng thái hình ảnh = "Hoạt động"

### 3. Xóa không được

- Kiểm tra quyền xóa file
- Kiểm tra hình ảnh có thuộc hạng phòng đúng không

## 📝 Ghi chú

- Hình ảnh được lưu trực tiếp vào file system (không dùng cloud storage)
- Cần backup thư mục `images/roomtypes/` khi deploy
- Có thể cải thiện bằng cách sử dụng cloud storage (AWS S3, Google Cloud Storage)
- Có thể thêm tính năng resize/compress hình ảnh tự động


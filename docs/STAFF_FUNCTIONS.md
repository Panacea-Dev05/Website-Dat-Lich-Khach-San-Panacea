# CHỨC NĂNG NHÂN VIÊN TRONG HỆ THỐNG QUẢN LÝ KHÁCH SẠN

## TỔNG QUAN

Nhân viên có quyền hạn hạn chế so với Admin, chỉ được thực hiện các chức năng cần thiết cho công việc hàng ngày.

## CÁC CHỨC NĂNG NHÂN VIÊN CÓ THỂ THỰC HIỆN

### 1. QUẢN LÝ ĐẶT PHÒNG

- ✅ **Xem danh sách booking** - Tất cả booking trong hệ thống
- ✅ **Xác nhận booking** - Chuyển trạng thái từ "CHỜ XÁC NHẬN" sang "ĐÃ XÁC NHẬN"
- ✅ **Hủy booking** - Chuyển trạng thái sang "ĐÃ HỦY"
- ✅ **Check-in khách hàng** - Chuyển trạng thái sang "ĐÃ CHECK-IN"
- ✅ **Check-out khách hàng** - Chuyển trạng thái sang "ĐÃ CHECK-OUT"
- ✅ **Gán phòng cho booking** - Chọn phòng cụ thể cho booking
- ✅ **Xem chi tiết booking** - Thông tin đầy đủ về booking

### 2. QUẢN LÝ PHÒNG

- ✅ **Xem danh sách phòng** - Tất cả phòng và trạng thái
- ✅ **Thay đổi trạng thái phòng** - Chỉ được thay đổi trạng thái:
  - Sẵn sàng
  - Đang sử dụng
  - Bảo trì
  - Dọn dẹp
  - Đã đặt
- ❌ **Tạo phòng mới** - KHÔNG được phép
- ❌ **Sửa thông tin phòng** - KHÔNG được phép
- ❌ **Xóa phòng** - KHÔNG được phép

### 3. QUẢN LÝ THANH TOÁN

- ✅ **Xem danh sách thanh toán** - Tất cả thanh toán
- ✅ **Tạo thanh toán mới** - Chỉ thanh toán tiền mặt và chuyển khoản
- ✅ **Xác nhận thanh toán** - Chuyển trạng thái sang "THÀNH CÔNG"
- ✅ **Xem chi tiết thanh toán** - Thông tin đầy đủ về thanh toán
- ❌ **Tạo thanh toán online** - KHÔNG được phép (chỉ Admin)

### 4. QUẢN LÝ KHO (ROOM USAGE TRACKING)

- ✅ **Xem danh sách vật phẩm** - Tất cả vật phẩm trong kho
- ✅ **Ghi nhận sử dụng đồ của phòng** - Thêm mới:
  - Mã đặt phòng (dropdown)
  - Tên khách hàng (tự động điền)
  - Số điện thoại (tự động điền)
  - Số phòng (tự động điền)
  - Tên vật phẩm sử dụng
  - Số lượng sử dụng
  - Ngày sử dụng
- ✅ **Xem lịch sử sử dụng** - Tất cả ghi nhận sử dụng
- ✅ **Tìm kiếm sử dụng** - Theo phòng, khách hàng, vật phẩm
- ❌ **Tạo vật phẩm mới** - KHÔNG được phép
- ❌ **Sửa thông tin vật phẩm** - KHÔNG được phép
- ❌ **Xóa vật phẩm** - KHÔNG được phép

### 5. QUẢN LÝ DỊCH VỤ

- ✅ **Xem danh sách dịch vụ** - Tất cả dịch vụ có sẵn
- ✅ **Xem dịch vụ đã đặt** - Dịch vụ đã được đặt cho các booking
- ✅ **Thêm dịch vụ cho booking** - Chỉ thêm mới:
  - Chọn booking
  - Chọn dịch vụ
  - Nhập số lượng
  - Nhập ghi chú
- ✅ **Xem chi tiết dịch vụ** - Thông tin chi tiết dịch vụ
- ❌ **Tạo dịch vụ mới** - KHÔNG được phép
- ❌ **Sửa thông tin dịch vụ** - KHÔNG được phép
- ❌ **Xóa dịch vụ** - KHÔNG được phép
- ❌ **Cập nhật giá dịch vụ** - KHÔNG được phép

### 6. YÊU CẦU VẬT TƯ

- ✅ **Tạo yêu cầu mua sắm** - Yêu cầu bổ sung vật phẩm:
  - Tên vật phẩm
  - Số lượng cần mua
  - Lý do yêu cầu
  - Ưu tiên (Cao/Trung bình/Thấp)
  - Ngày cần sử dụng
- ✅ **Xem trạng thái yêu cầu** - Theo dõi yêu cầu đã gửi
- ❌ **Duyệt yêu cầu** - KHÔNG được phép (chỉ Admin)

### 7. THÔNG TIN KHÁCH HÀNG

- ✅ **Xem danh sách khách hàng** - Tất cả khách hàng
- ✅ **Xem chi tiết khách hàng** - Thông tin đầy đủ
- ✅ **Cập nhật thông tin khách hàng** - Chỉnh sửa thông tin cơ bản
- ❌ **Tạo khách hàng mới** - KHÔNG được phép (tự động tạo khi đặt phòng)
- ❌ **Xóa khách hàng** - KHÔNG được phép

## CÁC CHỨC NĂNG NHÂN VIÊN KHÔNG CÓ

### QUẢN LÝ HỆ THỐNG

- ❌ **Tạo phòng mới** - Chỉ Admin
- ❌ **Sửa thông tin phòng** - Chỉ Admin
- ❌ **Xóa phòng** - Chỉ Admin
- ❌ **Tạo loại phòng mới** - Chỉ Admin
- ❌ **Sửa loại phòng** - Chỉ Admin
- ❌ **Xóa loại phòng** - Chỉ Admin

### QUẢN LÝ NGƯỜI DÙNG

- ❌ **Tạo nhân viên mới** - Chỉ Admin
- ❌ **Sửa thông tin nhân viên** - Chỉ Admin
- ❌ **Xóa nhân viên** - Chỉ Admin
- ❌ **Phân quyền nhân viên** - Chỉ Admin

### QUẢN LÝ DỊCH VỤ

- ❌ **Tạo dịch vụ mới** - Chỉ Admin
- ❌ **Sửa thông tin dịch vụ** - Chỉ Admin
- ❌ **Xóa dịch vụ** - Chỉ Admin
- ❌ **Cập nhật giá dịch vụ** - Chỉ Admin

### QUẢN LÝ KHO

- ❌ **Tạo vật phẩm mới** - Chỉ Admin
- ❌ **Sửa thông tin vật phẩm** - Chỉ Admin
- ❌ **Xóa vật phẩm** - Chỉ Admin
- ❌ **Nhập kho** - Chỉ Admin
- ❌ **Xuất kho** - Chỉ Admin

### BÁO CÁO VÀ THỐNG KÊ

- ❌ **Xem báo cáo doanh thu** - Chỉ Admin
- ❌ **Xem báo cáo phòng** - Chỉ Admin
- ❌ **Xem báo cáo khách hàng** - Chỉ Admin
- ❌ **Xuất báo cáo** - Chỉ Admin

### CÀI ĐẶT HỆ THỐNG

- ❌ **Cài đặt khách sạn** - Chỉ Admin
- ❌ **Cài đặt email** - Chỉ Admin
- ❌ **Cài đặt thanh toán** - Chỉ Admin
- ❌ **Cài đặt bảo mật** - Chỉ Admin
- ❌ **Backup dữ liệu** - Chỉ Admin

## KẾT LUẬN

Nhân viên được thiết kế để thực hiện các công việc hàng ngày cần thiết cho hoạt động của khách sạn, nhưng không có quyền thay đổi cấu trúc hệ thống hoặc dữ liệu quan trọng. Tất cả các chức năng quản trị đều được dành riêng cho Admin.

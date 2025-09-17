// JavaScript cho quản lý lịch sử sử dụng phòng

document.addEventListener("DOMContentLoaded", function () {
  // Khởi tạo form ghi nhận sử dụng phòng
  initRoomUsageForm();

  // Khởi tạo tìm kiếm
  initSearch();

  // Thiết lập ngày mặc định
  setDefaultDateTime();
});

// Khởi tạo form ghi nhận sử dụng phòng
function initRoomUsageForm() {
  const form = document.getElementById("roomUsageForm");
  if (form) {
    form.addEventListener("submit", function (e) {
      e.preventDefault();
      submitRoomUsage();
    });
  }

  // Xử lý khi chọn mã đặt phòng
  const maDatPhongSelect = document.getElementById("maDatPhongSelect");
  const tenKhachHangInput = document.getElementById("tenKhachHangInput");
  const soDienThoaiInput = document.querySelector('input[name="soDienThoai"]');
  const soPhongInput = document.querySelector('input[name="soPhong"]');

  if (maDatPhongSelect) {
    maDatPhongSelect.addEventListener("change", function () {
      const selectedOption = this.options[this.selectedIndex];
      if (selectedOption.value && selectedOption.text) {
        // Tách tên khách hàng từ text (format: "BK001 - Nguyễn Văn A")
        const parts = selectedOption.text.split(" - ");
        if (parts.length >= 2) {
          tenKhachHangInput.value = parts[1]; // Tên khách hàng
        }

        // Lấy thông tin booking chi tiết từ server
        fetch(`/nhanvien/quanlykho/api/booking/${selectedOption.value}`)
          .then((response) => response.json())
          .then((data) => {
            if (data.success && data.data) {
              const booking = data.data;
              // Tự động điền thông tin khách hàng
              if (tenKhachHangInput) {
                const ho = (booking.khachHang && booking.khachHang.ho) || "";
                const ten = (booking.khachHang && booking.khachHang.ten) || "";
                tenKhachHangInput.value = (ho + " " + ten).trim();
              }
              if (soDienThoaiInput) {
                soDienThoaiInput.value = (booking.khachHang && booking.khachHang.soDienThoai) || "";
              }
              if (soPhongInput) {
                // Booking không có soPhong trực tiếp, có thể lấy từ roomType hoặc để trống
                soPhongInput.value = "";
              }
            }
          })
          .catch((error) => {
            console.error("Error fetching booking details:", error);
          });
      } else {
        // Reset các trường khi không chọn gì
        if (tenKhachHangInput) tenKhachHangInput.value = "";
        if (soDienThoaiInput) soDienThoaiInput.value = "";
        if (soPhongInput) soPhongInput.value = "";
      }
    });
  }
}

// Gửi form ghi nhận sử dụng phòng
function submitRoomUsage() {
  const form = document.getElementById("roomUsageForm");
  const formData = new FormData(form);

  // Lấy thông tin vật phẩm từ select
  const vatPhamSelect = form.querySelector('select[name="vatPhamId"]');
  const selectedOption = vatPhamSelect.options[vatPhamSelect.selectedIndex];

  const requestData = {
    soPhong: formData.get("soPhong"),
    tenVatPham: selectedOption.text,
    maVatPham: selectedOption.value,
    soLuongSuDung: parseInt(formData.get("soLuongSuDung")),
    donViTinh: formData.get("donViTinh"),
    loaiSuDung: formData.get("loaiSuDung"),
    ngaySuDung: formData.get("ngaySuDung"),
    ghiChu: formData.get("ghiChu"),
    // Thông tin booking
    maDatPhong: formData.get("maDatPhong"),
    tenKhachHang: formData.get("tenKhachHang"),
    soDienThoai: formData.get("soDienThoai"),
  };

  // Hiển thị loading
  const submitBtn = document.getElementById("addRoomUsageBtn");
  const originalText = submitBtn.innerHTML;
  submitBtn.innerHTML =
    '<span class="material-icons">hourglass_empty</span>Đang ghi nhận...';
  submitBtn.disabled = true;

  fetch("/nhanvien/quanlykho/api/room-usage", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(requestData),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        showMessage("Ghi nhận sử dụng đồ thành công!", "success");
        form.reset();
        setDefaultDateTime();
        loadRoomUsageHistory();
      } else {
        showMessage("Lỗi: " + data.message, "error");
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      showMessage("Lỗi khi ghi nhận sử dụng: " + error.message, "error");
    })
    .finally(() => {
      submitBtn.innerHTML = originalText;
      submitBtn.disabled = false;
    });
}

// Tải lại lịch sử sử dụng phòng
function loadRoomUsageHistory() {
  fetch("/nhanvien/quanlykho/api/room-usage")
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        updateRoomUsageTable(data.data);
      } else {
        console.error("Error loading room usage history:", data.message);
      }
    })
    .catch((error) => {
      console.error("Error loading room usage history:", error);
    });
}

// Cập nhật bảng lịch sử sử dụng
function updateRoomUsageTable(usageHistory) {
  const tbody = document.querySelector("#roomUsageTable tbody");
  if (!tbody) return;

  tbody.innerHTML = "";

  if (usageHistory && usageHistory.length > 0) {
    usageHistory.forEach((usage) => {
      const row = document.createElement("tr");
      row.innerHTML = `
                <td>${usage.maDatPhong || "N/A"}</td>
                <td>${usage.tenKhachHang || "N/A"}</td>
                <td>${usage.soPhong || "N/A"}</td>
                <td>${usage.tenVatPham || "N/A"}</td>
                <td>${usage.loaiSuDung || "N/A"}</td>
                <td>${usage.soLuongSuDung || 0}</td>
                <td>${usage.donViTinh || "N/A"}</td>
                <td>${formatDateTime(usage.ngaySuDung)}</td>
                <td>${usage.nhanVienGhiNhan || "N/A"}</td>
                <td>${usage.ghiChu || "N/A"}</td>
            `;
      tbody.appendChild(row);
    });
  } else {
    const row = document.createElement("tr");
    row.innerHTML =
      '<td colspan="10" class="text-center">Không có dữ liệu</td>';
    tbody.appendChild(row);
  }
}

// Tìm kiếm lịch sử sử dụng
function searchRoomUsage() {
  const searchTerm = document.getElementById("roomUsageSearch").value;
  const roomNumber = document.getElementById("roomNumberSearch").value;
  const bookingCode = document.getElementById("bookingSearch").value;
  const customerName = document.getElementById("customerSearch").value;

  let url = "/nhanvien/quanlykho/api/room-usage/search?";
  const params = new URLSearchParams();

  if (searchTerm && searchTerm.trim() !== "") {
    params.append("tenVatPham", searchTerm.trim());
  }

  if (roomNumber && roomNumber.trim() !== "") {
    params.append("soPhong", roomNumber.trim());
  }

  if (bookingCode && bookingCode.trim() !== "") {
    params.append("maDatPhong", bookingCode.trim());
  }

  if (customerName && customerName.trim() !== "") {
    params.append("tenKhachHang", customerName.trim());
  }

  url += params.toString();

  fetch(url)
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        updateRoomUsageTable(data.data);
        showMessage("Tìm kiếm thành công", "success");
      } else {
        showMessage("Lỗi tìm kiếm: " + data.message, "error");
      }
    })
    .catch((error) => {
      console.error("Error searching:", error);
      showMessage("Lỗi tìm kiếm: " + error.message, "error");
    });
}

// Reset tìm kiếm
function resetRoomUsageSearch() {
  document.getElementById("roomUsageSearch").value = "";
  document.getElementById("roomNumberSearch").value = "";
  document.getElementById("bookingSearch").value = "";
  document.getElementById("customerSearch").value = "";
  loadRoomUsageHistory();
}

// Khởi tạo tìm kiếm
function initSearch() {
  const searchInput = document.getElementById("roomUsageSearch");
  const roomNumberInput = document.getElementById("roomNumberSearch");
  const bookingInput = document.getElementById("bookingSearch");
  const customerInput = document.getElementById("customerSearch");

  if (searchInput) {
    searchInput.addEventListener("keypress", function (e) {
      if (e.key === "Enter") {
        searchRoomUsage();
      }
    });
  }

  if (roomNumberInput) {
    roomNumberInput.addEventListener("keypress", function (e) {
      if (e.key === "Enter") {
        searchRoomUsage();
      }
    });
  }

  if (bookingInput) {
    bookingInput.addEventListener("keypress", function (e) {
      if (e.key === "Enter") {
        searchRoomUsage();
      }
    });
  }

  if (customerInput) {
    customerInput.addEventListener("keypress", function (e) {
      if (e.key === "Enter") {
        searchRoomUsage();
      }
    });
  }
}

// Thiết lập ngày giờ mặc định
function setDefaultDateTime() {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  const hours = String(now.getHours()).padStart(2, "0");
  const minutes = String(now.getMinutes()).padStart(2, "0");

  const dateTimeString = `${year}-${month}-${day}T${hours}:${minutes}`;

  const dateTimeInput = document.querySelector('input[name="ngaySuDung"]');
  if (dateTimeInput && !dateTimeInput.value) {
    dateTimeInput.value = dateTimeString;
  }
}

// Format ngày giờ
function formatDateTime(dateTimeString) {
  if (!dateTimeString) return "N/A";

  try {
    const date = new Date(dateTimeString);
    return date.toLocaleString("vi-VN", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch (error) {
    return dateTimeString;
  }
}

// Hiển thị thông báo
function showMessage(message, type) {
  // Tạo thông báo
  const messageDiv = document.createElement("div");
  messageDiv.className = `message ${type}`;
  messageDiv.innerHTML = `
        <span class="material-icons">${
          type === "success" ? "check_circle" : "error"
        }</span>
        <span>${message}</span>
    `;

  // Thêm vào trang
  document.body.appendChild(messageDiv);

  // Tự động ẩn sau 3 giây
  setTimeout(() => {
    if (messageDiv.parentNode) {
      messageDiv.parentNode.removeChild(messageDiv);
    }
  }, 3000);
}

// CSS cho thông báo
const style = document.createElement("style");
style.textContent = `
    .message {
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 12px 20px;
        border-radius: 4px;
        color: white;
        font-weight: 500;
        z-index: 1000;
        display: flex;
        align-items: center;
        gap: 8px;
        box-shadow: 0 2px 8px rgba(0,0,0,0.2);
    }
    
    .message.success {
        background-color: #4caf50;
    }
    
    .message.error {
        background-color: #f44336;
    }
`;
document.head.appendChild(style);

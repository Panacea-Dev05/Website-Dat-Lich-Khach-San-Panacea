/**
 * Quản lý Khách sạn - Admin Dashboard
 * Xử lý các chức năng quản lý thông tin khách sạn và tiện ích
 */

document.addEventListener("DOMContentLoaded", () => {
  // Sidebar toggle for mobile
  const mobileToggle = document.getElementById("mobileToggle");
  const sidebar = document.getElementById("sidebar");

  if (mobileToggle && sidebar) {
    mobileToggle.addEventListener("click", () => {
      sidebar.classList.toggle("open");
    });
  }
  
  // Initialize hotel info form functionality
  initializeHotelInfoForm();
});

// Khởi tạo chức năng form thông tin khách sạn
function initializeHotelInfoForm() {
  const btnEditHotel = document.getElementById('btnEditHotel');
  const btnCancelEdit = document.getElementById('btnCancelEdit');
  const btnSaveHotel = document.getElementById('btnSaveHotel');
  const hotelInfoForm = document.getElementById('hotelInfoForm');
  const formActions = document.querySelector('.form-actions');
  
  if (btnEditHotel) {
    btnEditHotel.addEventListener('click', function() {
      enableEditMode();
    });
  }
  
  if (btnCancelEdit) {
    btnCancelEdit.addEventListener('click', function() {
      disableEditMode();
    });
  }
  
  if (hotelInfoForm) {
    hotelInfoForm.addEventListener('submit', function(e) {
      e.preventDefault();
      saveHotelInfo();
    });
  }
}

// Chức năng bật/tắt chế độ chỉnh sửa
function enableEditMode() {
  const formInputs = document.querySelectorAll('#hotelInfoForm input, #hotelInfoForm select, #hotelInfoForm textarea');
  const formActions = document.querySelector('.form-actions');
  const btnEditHotel = document.getElementById('btnEditHotel');
  
  formInputs.forEach(input => {
    if (input.type !== 'hidden') {
      input.removeAttribute('readonly');
      input.removeAttribute('disabled');
    }
  });
  
  if (formActions) {
    formActions.style.display = 'block';
  }
  
  if (btnEditHotel) {
    btnEditHotel.style.display = 'none';
  }
}

function disableEditMode() {
  const formInputs = document.querySelectorAll('#hotelInfoForm input, #hotelInfoForm select, #hotelInfoForm textarea');
  const formActions = document.querySelector('.form-actions');
  const btnEditHotel = document.getElementById('btnEditHotel');
  
  formInputs.forEach(input => {
    if (input.type !== 'hidden') {
      if (input.tagName === 'SELECT') {
        input.setAttribute('disabled', 'disabled');
      } else {
        input.setAttribute('readonly', 'readonly');
      }
    }
  });
  
  if (formActions) {
    formActions.style.display = 'none';
  }
  
  if (btnEditHotel) {
    btnEditHotel.style.display = 'inline-flex';
  }
  
  // Reload trang để khôi phục dữ liệu gốc
  location.reload();
}

// Lưu thông tin khách sạn
function saveHotelInfo() {
  const hotelId = document.getElementById('hotelId').value;
  const data = {
    id: parseInt(hotelId),
    tenKhachSan: document.getElementById('tenKhachSan').value,
    maKhachSan: document.getElementById('maKhachSan').value,
    diaChi: document.getElementById('diaChi').value,
    thanhPho: document.getElementById('thanhPho').value,
    quocGia: document.getElementById('quocGia').value,
    soDienThoai: document.getElementById('soDienThoai').value,
    email: document.getElementById('email').value,
    website: document.getElementById('website').value,
    soSao: parseInt(document.getElementById('soSao').value),
    trangThai: document.getElementById('trangThai').value,
    thoiGianNhanPhong: document.getElementById('thoiGianNhanPhong').value,
    thoiGianTraPhong: document.getElementById('thoiGianTraPhong').value,
    moTa: document.getElementById('moTa').value,
    chinhSachHuy: document.getElementById('chinhSachHuy').value
  };
  
  fetch('/admin/hotels/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data)
  })
  .then(response => {
    if (response.ok) {
      alert('Cập nhật thông tin khách sạn thành công!');
      disableEditMode();
    } else {
      alert('Cập nhật thông tin khách sạn thất bại!');
    }
  })
  .catch(error => {
    console.error('Error:', error);
    alert('Có lỗi xảy ra khi cập nhật thông tin!');
  });
}

// Modal xem chi tiết khách sạn
const hotelDetailModal = new bootstrap.Modal(
  document.getElementById("hotelDetailModal")
);

// Mở modal quản lý tiện ích khi nhấn nút trong modal chi tiết
const btnAddAmenity = document.getElementById("btnAddAmenity");
const manageAmenitiesModal = new bootstrap.Modal(
  document.getElementById("manageAmenitiesModal")
);
let currentHotelIdForAmenity = null;
let currentHotelCodeForAmenity = null;

btnAddAmenity.addEventListener("click", function () {
  console.log(
    "Mở modal quản lý tiện ích, hotelId:",
    currentHotelIdForAmenity,
    "hotelCode:",
    currentHotelCodeForAmenity
  ); // Debug log
  if (!currentHotelIdForAmenity || !currentHotelCodeForAmenity) {
    alert("Không xác định được khách sạn! Vui lòng thử lại.");
    return;
  }
  openModalOverModal("hotelDetailModal", "manageAmenitiesModal");
});

// Render bảng tiện ích khách sạn
function renderAmenitiesTable(amenities) {
  console.log("Render amenities table:", amenities); // Debug log
  const tbody = document.getElementById("amenitiesTableBody");
  tbody.innerHTML = "";
  if (!amenities || amenities.length === 0) {
    tbody.innerHTML =
      '<tr><td colspan="7" class="text-center">Chưa có tiện ích nào</td></tr>';
    return;
  }
  amenities.forEach((a, idx) => {
    let trangThai = "";
    switch (a.trangThai) {
      case "HOAT_DONG":
        trangThai = "Hoạt động";
        break;
      case "TAM_NGUNG":
        trangThai = "Tạm ngưng";
        break;
      default:
        trangThai = a.trangThai || "";
    }
    tbody.innerHTML += `
      <tr>
        <td>${idx + 1}</td>
        <td>${a.tenTienIch || ""}</td>
        <td>${a.loaiTienIch || ""}</td>
        <td>${a.moTa || ""}</td>
        <td>${a.phiSuDung ? a.phiSuDung.toLocaleString() : ""}</td>
        <td>${trangThai}</td>
        <td>
          <button class="btn btn-sm btn-warning" onclick="editAmenity(${
            a.id
          })">Sửa</button>
          <button class="btn btn-sm btn-danger" onclick="deleteAmenity(${
            a.id
          })">Xóa</button>
        </td>
      </tr>
    `;
  });
  console.log("Đã render xong amenities table"); // Debug log
}

// Khi xem chi tiết khách sạn, load luôn tiện ích vào bảng
window.viewHotel = function (id) {
  window.currentHotelDetailId = id;
  // Set hotelId cho việc thêm tiện ích
  currentHotelIdForAmenity = id;
  console.log("Set hotelId cho tiện ích:", id); // Debug log
  fetch(`/api/hotels/${id}`)
    .then((res) => res.json())
    .then((hotel) => {
      // Set hotelCode cho việc thêm tiện ích theo mã
      currentHotelCodeForAmenity = hotel.maKhachSan;
      console.log("Set hotelCode cho tiện ích:", hotel.maKhachSan); // Debug log

      document.getElementById("detailTenKhachSan").innerText =
        hotel.tenKhachSan || "";
      document.getElementById("detailDiaChi").innerText = hotel.diaChi || "";
      document.getElementById("detailSoDienThoai").innerText =
        hotel.soDienThoai || "";
      document.getElementById("detailEmail").innerText = hotel.email || "";
      let trangThai = "";
      switch (hotel.trangThai) {
        case "HOAT_DONG":
          trangThai = "Hoạt động";
          break;
        case "BAO_TRI":
          trangThai = "Bảo trì";
          break;
        case "DONG_CUA":
          trangThai = "Đóng cửa";
          break;
        default:
          trangThai = hotel.trangThai || "";
      }
      document.getElementById("detailTrangThai").innerText = trangThai;
      hotelDetailModal.show();
      // Load amenities
      fetch(`/api/hotels/${id}/amenities`).then(async (res) => {
        if (res.ok) {
          const amenities = await res.json();
          console.log("Load amenities cho hotel", id, ":", amenities); // Debug log
          renderAmenitiesTable(amenities);
        } else {
          const errorText = await res.text();
          console.error(
            "Lỗi load amenities:",
            res.status,
            res.statusText,
            errorText
          ); // Debug log
          alert("Lỗi khi tải danh sách tiện ích: " + errorText);
        }
      });
    });
};

// Submit form thêm tiện ích
const addAmenityForm = document.getElementById("addAmenityForm");
addAmenityForm.addEventListener("submit", function (e) {
  e.preventDefault();
  console.log(
    "Hotel ID khi submit:",
    currentHotelIdForAmenity,
    "Hotel Code:",
    currentHotelCodeForAmenity
  ); // Debug log
  if (!currentHotelIdForAmenity || !currentHotelCodeForAmenity) {
    alert("Không xác định được khách sạn!");
    return;
  }
  const tenTienIch = document.getElementById("amenityTen").value.trim();
  if (!tenTienIch) {
    alert("Tên tiện ích không được để trống");
    return;
  }

  const loaiTienIch = document.getElementById("amenityLoai").value.trim();
  if (!loaiTienIch) {
    alert("Loại tiện ích không được để trống");
    return;
  }

  const trangThai = document.getElementById("amenityTrangThai").value;
  if (!trangThai) {
    alert("Trạng thái không được để trống");
    return;
  }

  const phiSuDungValue =
    Number(document.getElementById("amenityPhi").value) || 0;
  if (phiSuDungValue > 99999999.99) {
    alert("Phí sử dụng không được vượt quá 99,999,999.99");
    return;
  }

  const data = {
    tenTienIch: tenTienIch,
    loaiTienIch: loaiTienIch,
    moTa: document.getElementById("amenityMoTa").value || "",
    phiSuDung: phiSuDungValue,
    trangThai: trangThai,
  };
  console.log("Dữ liệu gửi:", data); // Debug log
  fetch(`/api/hotels/code/${currentHotelCodeForAmenity}/amenities`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  })
    .then(async (res) => {
      if (res.ok) {
        const result = await res.json();
        console.log("Thêm tiện ích thành công:", result); // Debug log
        addAmenityModal.hide();
        // Tải lại bảng tiện ích trong modal quản lý
        if (currentHotelIdForAmenity) {
          fetch(`/api/hotels/${currentHotelIdForAmenity}/amenities`).then(
            async (res) => {
              if (res.ok) {
                const amenities = await res.json();
                console.log("Reload amenities sau khi thêm:", amenities); // Debug log
                renderAmenitiesTable(amenities);
              } else {
                const errorText = await res.text();
                console.error(
                  "Lỗi reload amenities:",
                  res.status,
                  res.statusText,
                  errorText
                ); // Debug log
              }
            }
          );
        }
      } else {
        const errorText = await res.text();
        console.error(
          "Lỗi thêm tiện ích:",
          res.status,
          res.statusText,
          errorText
        ); // Debug log
        alert("Thêm tiện ích thất bại: " + errorText);
      }
    })
    .catch((error) => {
      console.error("Lỗi network:", error); // Debug log
      alert("Lỗi kết nối khi thêm tiện ích!");
    });
});

// Mở modal thêm tiện ích (nhỏ) khi nhấn nút trong modal quản lý tiện ích
const btnOpenAddAmenityModal = document.getElementById(
  "btnOpenAddAmenityModal"
);
const addAmenityModal = new bootstrap.Modal(
  document.getElementById("addAmenityModal")
);

// Hàm mở modal chồng modal: ẩn modal hiện tại, mở modal mới, khi modal mới đóng thì hiện lại modal trước
function openModalOverModal(currentModalId, nextModalId) {
  console.log("Mở modal over modal:", currentModalId, "->", nextModalId); // Debug log
  const currentModal = new bootstrap.Modal(
    document.getElementById(currentModalId)
  );
  const nextModal = new bootstrap.Modal(document.getElementById(nextModalId));
  // Ẩn modal hiện tại
  document.getElementById(currentModalId).classList.remove("show");
  document.body.classList.remove("modal-open");
  document.querySelectorAll(".modal-backdrop").forEach((e) => e.remove());
  // Mở modal mới
  nextModal.show();
  // Khi modal mới đóng, hiện lại modal trước
  const nextModalEl = document.getElementById(nextModalId);
  const handler = function () {
    console.log("Đóng modal", nextModalId, ", hiện lại modal", currentModalId); // Debug log
    currentModal.show();
    nextModalEl.removeEventListener("hidden.bs.modal", handler);
  };
  nextModalEl.addEventListener("hidden.bs.modal", handler);
}

if (btnOpenAddAmenityModal) {
  btnOpenAddAmenityModal.addEventListener("click", function () {
    console.log(
      "Mở modal thêm tiện ích, hotelId:",
      currentHotelIdForAmenity,
      "hotelCode:",
      currentHotelCodeForAmenity
    ); // Debug log
    if (!currentHotelIdForAmenity || !currentHotelCodeForAmenity) {
      alert("Không xác định được khách sạn! Vui lòng thử lại.");
      return;
    }
    document.getElementById("addAmenityForm").reset();
    openModalOverModal("manageAmenitiesModal", "addAmenityModal");
  });
}

// Khi đóng modal thêm tiện ích, hiện lại modal quản lý tiện ích
const addAmenityModalEl = document.getElementById("addAmenityModal");
addAmenityModalEl.addEventListener("hidden.bs.modal", function () {
  console.log("Đóng modal thêm tiện ích, hiện lại modal quản lý"); // Debug log
  manageAmenitiesModal.show();
});

// Khi đóng modal quản lý tiện ích, hiện lại modal chi tiết khách sạn
const manageAmenitiesModalEl = document.getElementById("manageAmenitiesModal");
manageAmenitiesModalEl.addEventListener("hidden.bs.modal", function () {
  console.log("Đóng modal quản lý tiện ích, hiện lại modal chi tiết khách sạn"); // Debug log
  hotelDetailModal.show();
});

// Khi đóng modal chi tiết khách sạn, reset hotelId
const hotelDetailModalEl = document.getElementById("hotelDetailModal");
hotelDetailModalEl.addEventListener("hidden.bs.modal", function () {
  console.log("Đóng modal chi tiết khách sạn, reset hotelId và hotelCode"); // Debug log
  currentHotelIdForAmenity = null;
  currentHotelCodeForAmenity = null;
});

window.deleteAmenity = function(id) {
  if (!confirm('Bạn có chắc chắn muốn xóa tiện ích này?')) return;
  fetch(`/api/hotels/amenities/${id}`, {
    method: 'DELETE'
  })
    .then(async (res) => {
      if (res.ok) {
        alert('Xóa tiện ích thành công!');
        // Reload lại bảng tiện ích
        if (currentHotelIdForAmenity) {
          fetch(`/api/hotels/${currentHotelIdForAmenity}/amenities`).then(async (res) => {
            if (res.ok) {
              const amenities = await res.json();
              renderAmenitiesTable(amenities);
            }
          });
        }
      } else {
        const errorText = await res.text();
        alert('Xóa tiện ích thất bại: ' + errorText);
      }
    })
    .catch((err) => {
      alert('Lỗi kết nối khi xóa tiện ích!');
    });
};

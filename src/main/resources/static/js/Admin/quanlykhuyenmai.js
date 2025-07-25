document.addEventListener("DOMContentLoaded", () => {
  // Sidebar toggle for mobile
  const mobileToggle = document.getElementById("mobileToggle");
  const sidebar = document.getElementById("sidebar");

  if (mobileToggle && sidebar) {
    mobileToggle.addEventListener("click", () => {
      sidebar.classList.toggle("open");
    });
  }

  // --- Quản lý khuyến mãi ---
  const btnAdd = document.getElementById("btnAddPromotion");
  const form = document.getElementById("promotionForm");
  const formTitle = document.getElementById("formTitle");
  const submitBtn = document.getElementById("submitBtn");
  const cancelBtn = document.getElementById("cancelBtn");
  const tableBody = document.getElementById("promotionTableBody");

  // Hiển thị form thêm mới
  if (btnAdd && form) {
    btnAdd.addEventListener("click", () => {
      form.reset && form.reset();
      form.style.display = "block";
      formTitle.textContent = "Thêm khuyến mãi mới";
      form.setAttribute("data-mode", "add");
      form.promotionId.value = "";
    });
  }

  // Ẩn form khi bấm Hủy
  if (cancelBtn && form) {
    cancelBtn.addEventListener("click", () => {
      form.style.display = "none";
    });
  }

  // Sửa khuyến mãi
  window.editPromotion = function(id) {
    fetch(`/admin/promotions/${id}`)
      .then(res => res.json())
      .then(data => {
        form.style.display = "block";
        formTitle.textContent = "Chỉnh sửa khuyến mãi";
        form.setAttribute("data-mode", "edit");
        form.promotionId.value = data.id;
        form.maKhuyenMai.value = data.maKhuyenMai;
        form.tenKhuyenMai.value = data.tenKhuyenMai;
        form.moTa.value = data.moTa || "";
        form.loaiGiamGia.value = data.loaiGiamGia;
        form.giaTriGiam.value = data.giaTriGiam;
        form.giamToiDa.value = data.giamToiDa || "";
        form.ngayBatDau.value = data.ngayBatDau;
        form.ngayKetThuc.value = data.ngayKetThuc;
        form.soLuongToiDa.value = data.soLuongToiDa || "";
        form.dieuKienApDung.value = data.dieuKienApDung || "";
        form.trangThai.value = data.trangThai;
      });
  };

  // Xóa khuyến mãi
  window.deletePromotion = function(id) {
    if (!confirm("Bạn có chắc chắn muốn xóa khuyến mãi này?")) return;
    fetch(`/admin/promotions/${id}`, { method: "DELETE" })
      .then(res => {
        if (res.ok) {
          alert("Đã xóa thành công!");
          location.reload();
        } else {
          alert("Xóa thất bại!");
        }
      });
  };

  // Submit form thêm/sửa
  if (form) {
    form.addEventListener("submit", function(e) {
      e.preventDefault();
      const mode = form.getAttribute("data-mode");
      const id = form.promotionId.value;
      const dto = {
        maKhuyenMai: form.maKhuyenMai.value,
        tenKhuyenMai: form.tenKhuyenMai.value,
        moTa: form.moTa.value,
        loaiGiamGia: form.loaiGiamGia.value,
        giaTriGiam: form.giaTriGiam.value,
        giamToiDa: form.giamToiDa.value,
        ngayBatDau: form.ngayBatDau.value,
        ngayKetThuc: form.ngayKetThuc.value,
        soLuongToiDa: form.soLuongToiDa.value,
        dieuKienApDung: form.dieuKienApDung.value,
        trangThai: form.trangThai.value
      };
      let url = "/admin/promotions";
      let method = "POST";
      if (mode === "edit" && id) {
        url = `/admin/promotions/${id}`;
        method = "PUT";
      }
      fetch(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dto)
      })
        .then(res => res.json())
        .then(data => {
          alert("Lưu thành công!");
          location.reload();
        })
        .catch(() => alert("Có lỗi xảy ra!"));
    });
  }
});

// Global variables
let currentEditingItemId = null;

// Debug log to confirm script is loaded
console.log("QuanLyKho.js loaded successfully");

// Make functions globally available
window.openCreateItemModal = function () {
  console.log("openCreateItemModal called");
  document.getElementById("createItemModal").style.display = "block";
  document.body.style.overflow = "hidden";
  resetCreateForm();
};

window.closeCreateItemModal = function () {
  document.getElementById("createItemModal").style.display = "none";
  document.body.style.overflow = "auto";
};

window.closeUpdateItemModal = function () {
  document.getElementById("updateItemModal").style.display = "none";
  document.body.style.overflow = "auto";
};

function openUpdateItemModal(itemId) {
  document.getElementById("updateItemModal").style.display = "block";
  document.body.style.overflow = "hidden";
  loadItemForEdit(itemId);
}

// Close modal when clicking outside
window.onclick = function (event) {
  const createModal = document.getElementById("createItemModal");
  const updateModal = document.getElementById("updateItemModal");

  if (event.target === createModal) {
    closeCreateItemModal();
  }
  if (event.target === updateModal) {
    closeUpdateItemModal();
  }
};

// Form reset functions
function resetCreateForm() {
  const form = document.getElementById("createItemForm");
  if (form) {
    form.reset();
  }
}

function resetUpdateForm() {
  const form = document.getElementById("updateItemForm");
  if (form) {
    form.reset();
  }
}

// Validation functions
function validateCreateForm() {
  const tenVatPham = document
    .querySelector('#createItemForm input[name="tenVatPham"]')
    .value.trim();
  const maVatPham = document
    .querySelector('#createItemForm input[name="maVatPham"]')
    .value.trim();
  const loaiVatPham = document.querySelector(
    '#createItemForm select[name="loaiVatPham"]'
  ).value;

  if (!tenVatPham) {
    showNotification("Vui lòng nhập tên vật phẩm", "error");
    return false;
  }

  if (!maVatPham) {
    showNotification("Vui lòng nhập mã vật phẩm", "error");
    return false;
  }

  if (!loaiVatPham) {
    showNotification("Vui lòng chọn loại vật phẩm", "error");
    return false;
  }

  return true;
}

function validateUpdateForm() {
  const tenVatPham = document
    .querySelector('#updateItemForm input[name="tenVatPham"]')
    .value.trim();
  const maVatPham = document
    .querySelector('#updateItemForm input[name="maVatPham"]')
    .value.trim();
  const loaiVatPham = document.querySelector(
    '#updateItemForm select[name="loaiVatPham"]'
  ).value;

  if (!tenVatPham) {
    showNotification("Vui lòng nhập tên vật phẩm", "error");
    return false;
  }

  if (!maVatPham) {
    showNotification("Vui lòng nhập mã vật phẩm", "error");
    return false;
  }

  if (!loaiVatPham) {
    showNotification("Vui lòng chọn loại vật phẩm", "error");
    return false;
  }

  return true;
}

// CRUD operations
function createItem(itemData) {
  fetch("/admin/inventory/items", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(itemData),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        showNotification("Thêm vật phẩm thành công!", "success");
        closeCreateItemModal();
        location.reload();
      } else {
        showNotification(
          data.message || "Có lỗi xảy ra khi thêm vật phẩm",
          "error"
        );
      }
    })
    .catch((error) => {
      console.error("Error creating item:", error);
      showNotification("Có lỗi xảy ra khi thêm vật phẩm", "error");
    });
}

function loadItemForEdit(itemId) {
  currentEditingItemId = itemId;

  fetch(`/admin/inventory/items/${itemId}`)
    .then((response) => response.json())
    .then((data) => {
      if (data.success && data.data) {
        const item = data.data;

        document.querySelector(
          '#updateItemForm input[name="tenVatPham"]'
        ).value = item.tenVatPham || "";
        document.querySelector(
          '#updateItemForm input[name="maVatPham"]'
        ).value = item.maVatPham || "";
        document.querySelector(
          '#updateItemForm select[name="loaiVatPham"]'
        ).value = item.loaiVatPham || "";
        document.querySelector(
          '#updateItemForm input[name="soLuongTon"]'
        ).value = item.soLuongTon || 0;
        document.querySelector(
          '#updateItemForm input[name="soLuongToiThieu"]'
        ).value = item.soLuongToiThieu || 0;
        document.querySelector('#updateItemForm input[name="giaNhap"]').value =
          item.giaNhap || 0;
        document.querySelector(
          '#updateItemForm input[name="donViTinh"]'
        ).value = item.donViTinh || "";
        document.querySelector('#updateItemForm input[name="viTriKho"]').value =
          item.viTriKho || "";
        document.querySelector(
          '#updateItemForm input[name="nhaCungCap"]'
        ).value = item.nhaCungCap || "";
        document.querySelector(
          '#updateItemForm input[name="hanSuDung"]'
        ).value = item.hanSuDung || "";
        document.querySelector(
          '#updateItemForm select[name="trangThai"]'
        ).value = item.trangThai || "HOAT_DONG";
      } else {
        showNotification("Không thể tải thông tin vật phẩm", "error");
      }
    })
    .catch((error) => {
      console.error("Error loading item:", error);
      showNotification("Có lỗi xảy ra khi tải thông tin vật phẩm", "error");
    });
}

function updateItem(itemId, itemData) {
  fetch(`/admin/inventory/items/${itemId}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(itemData),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        showNotification("Cập nhật vật phẩm thành công!", "success");
        closeUpdateItemModal();
        location.reload();
      } else {
        showNotification(
          data.message || "Có lỗi xảy ra khi cập nhật vật phẩm",
          "error"
        );
      }
    })
    .catch((error) => {
      console.error("Error updating item:", error);
      showNotification("Có lỗi xảy ra khi cập nhật vật phẩm", "error");
    });
}

// Search and filter functions
function searchItems() {
  const searchValue = document.getElementById("searchInput").value.trim();
  const typeFilter = document.getElementById("categoryFilter").value;
  const statusFilter = document.getElementById("statusFilter").value;

  const params = new URLSearchParams();
  if (searchValue) params.append("search", searchValue);
  if (typeFilter) params.append("type", typeFilter);
  if (statusFilter) params.append("status", statusFilter);

  window.location.href = `/admin/inventory?${params.toString()}`;
}

function filterByType() {
  searchItems();
}

function filterByStatus() {
  searchItems();
}

function resetSearch() {
  document.getElementById("searchInput").value = "";
  document.getElementById("categoryFilter").value = "";
  document.getElementById("statusFilter").value = "";
  window.location.href = "/admin/inventory";
}

function resetForm() {
  const form = document.getElementById("itemForm");
  if (form) {
    form.reset();
  }

  const formTitle = document.querySelector(".form-title");
  if (formTitle) {
    formTitle.textContent = "Thêm vật phẩm mới";
  }

  const submitBtn = document.querySelector('#itemForm button[type="submit"]');
  if (submitBtn) {
    submitBtn.textContent = "Thêm vật phẩm";
  }
}

function changePageSize() {
  const pageSize = document.querySelector('select[name="pageSize"]').value;
  const url = new URL(window.location);
  url.searchParams.set("size", pageSize);
  window.location.href = url.toString();
}

function editItem(id) {
  openUpdateItemModal(id);
}

function deleteItem(id) {
  if (!confirm("Bạn có chắc chắn muốn xóa vật phẩm này?")) {
    return;
  }

  fetch(`/admin/inventory/items/${id}`, {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        showNotification("Xóa vật phẩm thành công!", "success");
        location.reload();
      } else {
        showNotification(
          data.message || "Có lỗi xảy ra khi xóa vật phẩm",
          "error"
        );
      }
    })
    .catch((error) => {
      console.error("Error deleting item:", error);
      showNotification("Có lỗi xảy ra khi xóa vật phẩm", "error");
    });
}

// Notification system
function showNotification(message, type = "info") {
  const existingNotifications = document.querySelectorAll(".notification");
  existingNotifications.forEach((notification) => notification.remove());

  const notification = document.createElement("div");
  notification.className = `notification notification-${type}`;
  notification.innerHTML = `
        <span>${message}</span>
        <button class="notification-close" onclick="this.parentElement.remove()">&times;</button>
    `;

  const styles = {
    position: "fixed",
    top: "20px",
    right: "20px",
    padding: "15px 20px",
    borderRadius: "5px",
    color: "white",
    fontWeight: "bold",
    zIndex: "10000",
    minWidth: "300px",
    boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  };

  Object.assign(notification.style, styles);

  switch (type) {
    case "success":
      notification.style.backgroundColor = "#28a745";
      break;
    case "error":
      notification.style.backgroundColor = "#dc3545";
      break;
    case "warning":
      notification.style.backgroundColor = "#ffc107";
      notification.style.color = "#212529";
      break;
    case "info":
    default:
      notification.style.backgroundColor = "#17a2b8";
      break;
  }

  const closeBtn = notification.querySelector(".notification-close");
  if (closeBtn) {
    closeBtn.style.background = "none";
    closeBtn.style.border = "none";
    closeBtn.style.color = "inherit";
    closeBtn.style.fontSize = "18px";
    closeBtn.style.cursor = "pointer";
    closeBtn.style.marginLeft = "10px";
  }

  document.body.appendChild(notification);

  setTimeout(() => {
    if (notification.parentElement) {
      notification.remove();
    }
  }, 5000);
}

// DOM Content Loaded event listener
document.addEventListener("DOMContentLoaded", function () {
  const createForm = document.getElementById("createItemForm");
  if (createForm) {
    createForm.addEventListener("submit", function (e) {
      e.preventDefault();

      if (!validateCreateForm()) {
        return;
      }

      const formData = new FormData(this);
      const itemData = {
        tenVatPham: formData.get("tenVatPham"),
        maVatPham: formData.get("maVatPham"),
        loaiVatPham: formData.get("loaiVatPham"),
        soLuongTon: parseInt(formData.get("soLuongTon")) || 0,
        soLuongToiThieu: parseInt(formData.get("soLuongToiThieu")) || 0,
        giaNhap: parseFloat(formData.get("giaNhap")) || 0,
        donViTinh: formData.get("donViTinh"),
        viTriKho: formData.get("viTriKho"),
        nhaCungCap: formData.get("nhaCungCap"),
        hanSuDung: formData.get("hanSuDung") || null,
        trangThai: formData.get("trangThai"),
      };

      createItem(itemData);
    });
  }

  const updateForm = document.getElementById("updateItemForm");
  if (updateForm) {
    updateForm.addEventListener("submit", function (e) {
      e.preventDefault();

      if (!validateUpdateForm()) {
        return;
      }

      const formData = new FormData(this);
      const itemData = {
        tenVatPham: formData.get("tenVatPham"),
        maVatPham: formData.get("maVatPham"),
        loaiVatPham: formData.get("loaiVatPham"),
        soLuongTon: parseInt(formData.get("soLuongTon")) || 0,
        soLuongToiThieu: parseInt(formData.get("soLuongToiThieu")) || 0,
        giaNhap: parseFloat(formData.get("giaNhap")) || 0,
        donViTinh: formData.get("donViTinh"),
        viTriKho: formData.get("viTriKho"),
        nhaCungCap: formData.get("nhaCungCap"),
        hanSuDung: formData.get("hanSuDung") || null,
        trangThai: formData.get("trangThai"),
      };

      updateItem(currentEditingItemId, itemData);
    });
  }

  const transactionForm = document.getElementById("transactionForm");
  if (transactionForm) {
    transactionForm.addEventListener("submit", function (e) {
      e.preventDefault();

      const formData = new FormData(this);
      const transactionData = {
        vatPhamId: formData.get("vatPhamId"),
        loaiGiaoDich: formData.get("loaiGiaoDich"),
        soLuong: parseInt(formData.get("soLuong")),
        giaTri: parseFloat(formData.get("giaTri")),
        ngayGiaoDich: formData.get("ngayGiaoDich"),
        lyDo: formData.get("lyDo"),
      };

      fetch("/admin/inventory/transactions", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(transactionData),
      })
        .then((response) => response.json())
        .then((data) => {
          if (data.success) {
            showNotification("Ghi nhận giao dịch thành công!", "success");
            this.reset();
            location.reload();
          } else {
            showNotification(
              data.message || "Có lỗi xảy ra khi ghi nhận giao dịch",
              "error"
            );
          }
        })
        .catch((error) => {
          console.error("Error creating transaction:", error);
          showNotification("Có lỗi xảy ra khi ghi nhận giao dịch", "error");
        });
    });
  }

  const today = new Date().toISOString().split("T")[0];
  const transactionDateInput = document.querySelector(
    'input[name="ngayGiaoDich"]'
  );
  if (transactionDateInput) {
    transactionDateInput.value = today;
  }

  const searchInput = document.getElementById("searchInput");
  if (searchInput) {
    searchInput.addEventListener("keypress", function (e) {
      if (e.key === "Enter") {
        e.preventDefault();
        searchItems();
      }
    });
  }

  const typeFilter = document.getElementById("categoryFilter");
  if (typeFilter) {
    typeFilter.addEventListener("change", filterByType);
  }

  const statusFilter = document.getElementById("statusFilter");
  if (statusFilter) {
    statusFilter.addEventListener("change", filterByStatus);
  }

  const searchBtn = document.querySelector(".search-btn");
  if (searchBtn) {
    searchBtn.addEventListener("click", function (e) {
      e.preventDefault();
      searchItems();
    });
  }

  const resetBtn = document.querySelector(".reset-search-btn");
  if (resetBtn) {
    resetBtn.addEventListener("click", function (e) {
      e.preventDefault();
      resetSearch();
    });
  }
});

// Quản lý nhân viên - Frontend JavaScript
document.addEventListener('DOMContentLoaded', function() {
  const btnAdd = document.getElementById("btnAddStaff");
  const form = document.getElementById("staffForm");
  const overlay = document.getElementById("staffFormOverlay");
  const submitBtn = document.getElementById("submitBtn");
  const cancelBtn = document.getElementById("cancelBtn");
  const formTitle = document.getElementById("formTitle");
  const staffTableBody = document.getElementById('staffTableBody');
  
  let editingId = null;

  function showOverlayForm() {
      overlay.style.display = "flex";
      form.style.display = "flex";
  }
  function hideOverlayForm() {
      overlay.style.display = "none";
      form.style.display = "none";
  }

  btnAdd.addEventListener("click", () => {
      form.reset();
      editingId = null;
      formTitle.innerText = "Thêm nhân viên mới";
      showOverlayForm();
  });

  cancelBtn.addEventListener("click", () => {
      hideOverlayForm();
  });

  // Đóng overlay khi click ra ngoài form
  overlay.addEventListener("click", function(e) {
      if (e.target === overlay) hideOverlayForm();
  });

  submitBtn.addEventListener("click", async (e) => {
      e.preventDefault();

      const data = {
          maNhanVien: document.getElementById("maNhanVien").value,
          ho: document.getElementById("ho").value,
          ten: document.getElementById("ten").value,
          email: document.getElementById("email").value,
          soDienThoai: document.getElementById("soDienThoai").value,
          chucVu: document.getElementById("chucVu").value,
          trangThai: document.getElementById("trangThai").value
      };

      const url = editingId ? `/admin/staff/${editingId}` : `/admin/staff`;
      const method = editingId ? "PUT" : "POST";

      const response = await fetch(url, {
          method: method,
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(data)
      });

      if (response.ok) {
          window.location.reload();
      } else {
          alert("Đã có lỗi xảy ra.");
      }
  });

  window.editStaff = async (id) => {
      const res = await fetch(`/admin/staff/${id}`);
      const data = await res.json();
      editingId = id;
      form.style.display = "flex";
      overlay.style.display = "flex";
      formTitle.innerText = "Chỉnh sửa nhân viên";

      document.getElementById("maNhanVien").value = data.maNhanVien;
      document.getElementById("ho").value = data.ho;
      document.getElementById("ten").value = data.ten;
      document.getElementById("email").value = data.email;
      document.getElementById("soDienThoai").value = data.soDienThoai;
      document.getElementById("chucVu").value = data.chucVu;
      document.getElementById("trangThai").value = data.trangThai;
  };

  window.deleteStaff = async (id) => {
      if (!confirm("Bạn có chắc chắn muốn xóa nhân viên này không?")) return;
      const res = await fetch(`/admin/staff/${id}`, { method: "DELETE" });
      if (res.ok) {
          window.location.reload();
      } else {
          alert("Không thể xóa nhân viên.");
      }
  };

  // Mobile toggle functionality
  const mobileToggle = document.getElementById('mobileToggle');
  if (mobileToggle) {
      mobileToggle.addEventListener('click', function() {
          document.body.classList.toggle('sidebar-open');
      });
  }
});

// CSS cho animation
const style = document.createElement('style');
style.textContent = `
  @keyframes slideIn {
      from {
          transform: translateX(100%);
          opacity: 0;
      }
      to {
          transform: translateX(0);
          opacity: 1;
      }
  }
`;
document.head.appendChild(style);
// JavaScript xử lý thay đổi trạng thái phòng cho nhân viên
document.addEventListener("DOMContentLoaded", function () {
  const modal = document.getElementById("statusChangeModal");
  const closeModal = document.getElementById("closeModal");
  const cancelBtn = document.getElementById("cancelStatusChange");
  const confirmBtn = document.getElementById("confirmStatusChange");
  const statusChangeForm = document.getElementById("statusChangeForm");

  // Mở modal khi click vào nút thay đổi trạng thái
  document.addEventListener("click", function (e) {
    if (e.target.closest(".btn-status-change")) {
      const button = e.target.closest(".btn-status-change");
      const roomId = button.getAttribute("data-room-id");
      const currentStatus = button.getAttribute("data-current-status");
      const roomNumber = button.getAttribute("data-room-number");

      openStatusChangeModal(roomId, currentStatus, roomNumber);
    }
  });

  // Đóng modal
  function closeStatusChangeModal() {
    modal.style.display = "none";
    statusChangeForm.reset();
  }

  // Mở modal
  function openStatusChangeModal(roomId, currentStatus, roomNumber) {
    document.getElementById("roomId").value = roomId;
    document.getElementById("roomNumber").value = roomNumber;
    document.getElementById("currentStatus").value =
      getStatusDisplayName(currentStatus);
    document.getElementById("newStatus").value = "";
    document.getElementById("statusNote").value = "";

    modal.style.display = "block";
  }

  // Chuyển đổi mã trạng thái thành tên hiển thị
  function getStatusDisplayName(status) {
    const statusMap = {
      SAN_SANG: "Sẵn sàng",
      DANG_SU_DUNG: "Đang sử dụng",
      BAO_TRI: "Bảo trì",
      DON_DEP: "Dọn dẹp",
      DA_DAT: "Đã đặt",
    };
    return statusMap[status] || status;
  }

  // Xử lý thay đổi trạng thái
  function changeRoomStatus() {
    const roomId = document.getElementById("roomId").value;
    const newStatus = document.getElementById("newStatus").value;
    const statusNote = document.getElementById("statusNote").value;

    if (!newStatus) {
      alert("Vui lòng chọn trạng thái mới");
      return;
    }

    // Gửi request thay đổi trạng thái
    fetch(`/nhanvien/quanlyphong/api/rooms/${roomId}/status`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        trangThai: newStatus,
        ghiChu: statusNote,
      }),
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.success) {
          alert("Thay đổi trạng thái phòng thành công");
          closeStatusChangeModal();
          // Reload trang để cập nhật dữ liệu
          window.location.reload();
        } else {
          alert(
            "Lỗi: " + (data.message || "Không thể thay đổi trạng thái phòng")
          );
        }
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("Lỗi: Không thể kết nối đến server");
      });
  }

  // Event listeners
  closeModal.addEventListener("click", closeStatusChangeModal);
  cancelBtn.addEventListener("click", closeStatusChangeModal);
  confirmBtn.addEventListener("click", changeRoomStatus);

  // Đóng modal khi click bên ngoài
  window.addEventListener("click", function (e) {
    if (e.target === modal) {
      closeStatusChangeModal();
    }
  });

  // Đóng modal bằng phím ESC
  document.addEventListener("keydown", function (e) {
    if (e.key === "Escape" && modal.style.display === "block") {
      closeStatusChangeModal();
    }
  });
});

// CSS cho modal (inline để đảm bảo hoạt động)
const style = document.createElement("style");
style.textContent = `
    .modal {
        position: fixed;
        z-index: 1000;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0,0,0,0.5);
    }
    
    .modal-content {
        background-color: #fefefe;
        margin: 5% auto;
        padding: 0;
        border: 1px solid #888;
        width: 80%;
        max-width: 500px;
        border-radius: 8px;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    }
    
    .modal-header {
        padding: 20px;
        background-color: #f8f9fa;
        border-bottom: 1px solid #dee2e6;
        border-radius: 8px 8px 0 0;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
    
    .modal-header h3 {
        margin: 0;
        color: #333;
    }
    
    .close {
        color: #aaa;
        font-size: 28px;
        font-weight: bold;
        cursor: pointer;
        line-height: 1;
    }
    
    .close:hover {
        color: #000;
    }
    
    .modal-body {
        padding: 20px;
    }
    
    .form-group {
        margin-bottom: 15px;
    }
    
    .form-group label {
        display: block;
        margin-bottom: 5px;
        font-weight: 600;
        color: #333;
    }
    
    .form-group input,
    .form-group select,
    .form-group textarea {
        width: 100%;
        padding: 8px 12px;
        border: 1px solid #ddd;
        border-radius: 4px;
        font-size: 14px;
        box-sizing: border-box;
    }
    
    .form-group input[readonly] {
        background-color: #f8f9fa;
        color: #6c757d;
    }
    
    .modal-footer {
        padding: 20px;
        background-color: #f8f9fa;
        border-top: 1px solid #dee2e6;
        border-radius: 0 0 8px 8px;
        display: flex;
        justify-content: flex-end;
        gap: 10px;
    }
    
    .btn-cancel,
    .btn-confirm {
        padding: 8px 16px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 14px;
        font-weight: 500;
    }
    
    .btn-cancel {
        background-color: #6c757d;
        color: white;
    }
    
    .btn-cancel:hover {
        background-color: #5a6268;
    }
    
    .btn-confirm {
        background-color: #007bff;
        color: white;
    }
    
    .btn-confirm:hover {
        background-color: #0056b3;
    }
    
    .btn-status-change {
        background-color: #28a745;
        color: white;
        border: none;
        padding: 8px 12px;
        border-radius: 4px;
        cursor: pointer;
        display: inline-flex;
        align-items: center;
        gap: 4px;
    }
    
    .btn-status-change:hover {
        background-color: #218838;
    }
    
    .btn-status-change .material-icons {
        font-size: 18px;
    }
`;
document.head.appendChild(style);

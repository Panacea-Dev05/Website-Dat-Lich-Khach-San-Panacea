document.addEventListener("DOMContentLoaded", () => {
  // Sidebar toggle for mobile
  const mobileToggle = document.getElementById("mobileToggle");
  const sidebar = document.getElementById("sidebar");

  if (mobileToggle && sidebar) {
    mobileToggle.addEventListener("click", () => {
      sidebar.classList.toggle("open");
    });
  }
});

// Hàm gọi API lấy phòng đúng loại phòng khi mở modal xác nhận
function openConfirmModal(bookingId) {
    fetch(`/nhanvien/quanlydatphong/rooms/available?bookingId=${bookingId}`)
        .then(response => response.json())
        .then(data => {
            const select = document.getElementById('dropdown-room'); // id select phòng trong modal
            if (!select) return;
            select.innerHTML = '';
            if (data.length === 0) {
                select.innerHTML = '<option value="">Không còn phòng phù hợp</option>';
            } else {
                data.forEach(room => {
                    select.innerHTML += `<option value="${room.id}">${room.soPhong}</option>`;
                });
            }
        });
}

// Gắn sự kiện cho các nút mở modal xác nhận (giả sử có class .btn-open-confirm-modal và data-booking-id)
document.querySelectorAll('.btn-open-confirm-modal').forEach(btn => {
    btn.addEventListener('click', function() {
        const bookingId = this.getAttribute('data-booking-id');
        openConfirmModal(bookingId);
        // ... code mở modal ...
    });
});

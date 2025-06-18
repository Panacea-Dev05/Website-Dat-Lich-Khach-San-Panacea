// Sidebar toggle for mobile
const mobileToggle = document.getElementById('mobileToggle');
const sidebar = document.getElementById('sidebar');
mobileToggle.addEventListener('click', () => {
    sidebar.classList.toggle('open');
});

// Navigation menu click handler - thay đổi phần nội dung demo
const navLinks = sidebar.querySelectorAll('nav ul li a');
const contentSection = document.getElementById('contentSection');
const heading = document.querySelector('main h1');

const contentData = {
    rooms: `
        <button class="btn" id="btnAddRoom">Thêm phòng mới</button>
        <table aria-label="Danh sách phòng khách sạn">
          <thead>
            <tr>
              <th>STT</th>
              <th>Số phòng</th>
              <th>Loại phòng</th>
              <th>Tầng</th>
              <th>View</th>
              <th>Giá cơ bản</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr tabindex="0">
              <td>1</td>
              <td>101</td>
              <td>Standard</td>
              <td>1</td>
              <td>Hồ bơi</td>
              <td>1,200,000 VNĐ</td>
              <td>Sẵn sàng</td>
              <td>
                <button class="btn" style="padding: 6px 12px; font-size: 0.8rem;">Sửa</button>
                <button class="btn" style="padding: 6px 12px; font-size: 0.8rem; background:#ef4444; box-shadow:none;">Xóa</button>
              </td>
            </tr>
            <tr tabindex="0" style="background:#ecfdf5;">
              <td>2</td>
              <td>202</td>
              <td>Deluxe</td>
              <td>2</td>
              <td>Biển</td>
              <td>2,500,000 VNĐ</td>
              <td>Bảo trì</td>
              <td>
                <button class="btn" style="padding: 6px 12px; font-size: 0.8rem;">Sửa</button>
                <button class="btn" style="padding: 6px 12px; font-size: 0.8rem; background:#ef4444; box-shadow:none;">Xóa</button>
              </td>
            </tr>
          </tbody>
        </table>
      `,
    bookings: `<p style="padding:1rem; color:#065f46; font-weight:600;">Chức năng quản lý đặt phòng sẽ hiển thị ở đây.</p>`,
    customers: `<p style="padding:1rem; color:#065f46; font-weight:600;">Chức năng quản lý khách hàng sẽ hiển thị ở đây.</p>`,
    payments: `<p style="padding:1rem; color:#065f46; font-weight:600;">Chức năng quản lý thanh toán sẽ hiển thị ở đây.</p>`,
    promotions: `<p style="padding:1rem; color:#065f46; font-weight:600;">Chức năng quản lý khuyến mãi sẽ hiển thị ở đây.</p>`,
    reviews: `<p style="padding:1rem; color:#065f46; font-weight:600;">Chức năng quản lý đánh giá sẽ hiển thị ở đây.</p>`,
    staff: `<p style="padding:1rem; color:#065f46; font-weight:600;">Chức năng quản lý nhân viên sẽ hiển thị ở đây.</p>`,
    reports: `<p style="padding:1rem; color:#065f46; font-weight:600;">Báo cáo &amp; Thống kê sẽ hiển thị ở đây.</p>`,
    settings: `<p style="padding:1rem; color:#065f46; font-weight:600;">Cài đặt hệ thống sẽ hiển thị ở đây.</p>`
};

navLinks.forEach(link => {
    link.addEventListener('click', e => {
        e.preventDefault();
        navLinks.forEach(l => l.classList.remove('active'));
        link.classList.add('active');
        sidebar.classList.remove('open'); // Chạy tự đóng sidebar trên mobile

        const sec = link.dataset.section;
        heading.textContent = link.textContent.trim();
        contentSection.innerHTML = contentData[sec] || '<p>Chức năng đang cập nhật.</p>';
    });
});
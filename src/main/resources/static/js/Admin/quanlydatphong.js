// Dữ liệu mẫu booking
let bookings = [
    {id: 1, code: "BK001", customer: "Nguyễn Văn A", room: "101", checkIn: "2024-07-01", checkOut: "2024-07-05", guests: 2, status: "Confirmed"},
    {id: 2, code: "BK002", customer: "Trần Thị B", room: "202", checkIn: "2024-07-03", checkOut: "2024-07-06", guests: 3, status: "Pending"},
];
let editingBookingId = null;

const bookingTableBody = document.getElementById('bookingTableBody');
const bookingForm = document.getElementById('bookingForm');
const formTitle = document.getElementById('formTitle');
const submitBtn = document.getElementById('submitBtn');
const cancelBtn = document.getElementById('cancelBtn');
const btnAddBooking = document.getElementById('btnAddBooking');

const inputBookingCode = document.getElementById('bookingCode');
const inputCustomer = document.getElementById('customer');
const inputRoom = document.getElementById('room');
const inputCheckIn = document.getElementById('checkInDate');
const inputCheckOut = document.getElementById('checkOutDate');
const inputGuests = document.getElementById('numberOfGuests');
const selectStatus = document.getElementById('status');

function renderBookingTable() {
    bookingTableBody.innerHTML = '';
    bookings.forEach((bk, index) => {
        const tr = document.createElement('tr');
        tr.tabIndex = 0;
        tr.innerHTML = `
          <td>${index + 1}</td>
          <td>${bk.code}</td>
          <td>${bk.customer}</td>
          <td>${bk.room}</td>
          <td>${bk.checkIn}</td>
          <td>${bk.checkOut}</td>
          <td>${bk.guests}</td>
          <td>${statusText(bk.status)}</td>
          <td>
            <button class="btn btnEdit" data-id="${bk.id}" style="padding: 6px 12px; font-size: 0.8rem;">Sửa</button>
            <button class="btn btnDelete" data-id="${bk.id}" style="padding: 6px 12px; font-size: 0.8rem; background:#ef4444; box-shadow:none;">Hủy</button>
          </td>
        `;
        bookingTableBody.appendChild(tr);
    });
    attachTableButtonEvents();
}
function statusText(st){
    switch(st){
        case 'Confirmed': return 'Đã xác nhận';
        case 'Pending': return 'Chờ xác nhận';
        case 'CheckedIn': return 'Đã nhận phòng';
        case 'CheckedOut': return 'Đã trả phòng';
        case 'Cancelled': return 'Đã hủy';
        default: return st;
    }
}
function clearForm() {
    bookingForm.reset();
    editingBookingId = null;
    formTitle.textContent = "Tạo booking mới";
    submitBtn.textContent = "Lưu booking";
}
function showForm() {
    bookingForm.style.display = 'grid';
    btnAddBooking.style.display = 'none';
    window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});
    inputBookingCode.focus();
}
function hideForm() {
    bookingForm.style.display = 'none';
    btnAddBooking.style.display = 'inline-block';
    clearForm();
}
function attachTableButtonEvents() {
    document.querySelectorAll('.btnEdit').forEach(btn => {
        btn.onclick = e => {
            const id = Number(e.target.dataset.id);
            editBooking(id);
        };
    });
    document.querySelectorAll('.btnDelete').forEach(btn => {
        btn.onclick = e => {
            const id = Number(e.target.dataset.id);
            cancelBooking(id);
        };
    });
}
function editBooking(id) {
    const bk = bookings.find(b => b.id === id);
    if (!bk) return;
    editingBookingId = id;
    formTitle.textContent = "Cập nhật booking";
    submitBtn.textContent = "Cập nhật";
    inputBookingCode.value = bk.code;
    inputCustomer.value = bk.customer;
    inputRoom.value = bk.room;
    inputCheckIn.value = bk.checkIn;
    inputCheckOut.value = bk.checkOut;
    inputGuests.value = bk.guests;
    selectStatus.value = bk.status;
    showForm();
}
function cancelBooking(id) {
    const bk = bookings.find(b => b.id === id);
    if(!bk) return;
    if(confirm(`Bạn có chắc chắn muốn hủy booking ${bk.code} của khách ${bk.customer}?`)){
        bk.status = 'Cancelled';
        renderBookingTable();
    }
}
bookingForm.onsubmit = e => {
    e.preventDefault();
    const code = inputBookingCode.value.trim();
    const customer = inputCustomer.value.trim();
    const room = inputRoom.value.trim();
    const checkIn = inputCheckIn.value;
    const checkOut = inputCheckOut.value;
    const guests = Number(inputGuests.value);
    const status = selectStatus.value;

    if(!code || !customer || !room || !checkIn || !checkOut || !guests || !status){
        alert('Vui lòng điền đầy đủ thông tin.');
        return;
    }
    if(new Date(checkOut) <= new Date(checkIn)){
        alert('Ngày trả phải lớn hơn ngày nhận.');
        return;
    }
    // Check unique code on add new
    if(!editingBookingId && bookings.some(b => b.code === code)){
        alert('Mã booking đã tồn tại.');
        return;
    }
    if(editingBookingId){
        const idx = bookings.findIndex(b => b.id === editingBookingId);
        if(idx === -1) return;
        bookings[idx] = {id: editingBookingId, code, customer, room, checkIn, checkOut, guests, status};
        alert('Cập nhật booking thành công.');
    } else {
        const newId = bookings.length ? Math.max(...bookings.map(b => b.id)) +1 : 1;
        bookings.push({id: newId, code, customer, room, checkIn, checkOut, guests, status});
        alert('Tạo booking mới thành công.');
    }
    renderBookingTable();
    hideForm();
};
cancelBtn.onclick = () => {
    hideForm();
};
btnAddBooking.onclick = () => {
    clearForm();
    showForm();
};
// Sidebar toggle for mobile
const mobileToggle = document.getElementById('mobileToggle');
const sidebar = document.getElementById('sidebar');
mobileToggle.addEventListener('click', () => {
    sidebar.classList.toggle('open');
});
// Sidebar nav active link highlight and content change placeholder (for demo)
const navLinks = sidebar.querySelectorAll('nav ul li a');
const mainHeading = document.querySelector('main > h1');
const contentSection = document.getElementById('contentSection');

navLinks.forEach(link => {
    link.addEventListener('click', e => {
        e.preventDefault();
        navLinks.forEach(l => l.classList.remove('active'));
        link.classList.add('active');
        sidebar.classList.remove('open'); // auto-close sidebar on mobile
        const section = link.dataset.section;

        if(section !== 'bookings'){
            mainHeading.textContent = link.textContent.trim();
            contentSection.innerHTML = '<p style="padding:1rem; color:#065f46; font-weight:600;">Chức năng ' + link.textContent.trim() + ' sẽ được phát triển tiếp theo.</p>';
        } else {
            mainHeading.textContent = 'Quản lý đặt phòng';
            contentSection.innerHTML = `
            <button class="btn" id="btnAddBooking">Tạo booking mới</button>
            <table aria-label="Danh sách booking" id="bookingTable">
              <thead>
                <tr>
                  <th>STT</th>
                  <th>Mã booking</th>
                  <th>Khách hàng</th>
                  <th>Phòng</th>
                  <th>Ngày nhận</th>
                  <th>Ngày trả</th>
                  <th>Số người</th>
                  <th>Trạng thái</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody id="bookingTableBody"></tbody>
            </table>
            <form id="bookingForm" style="display:none; margin-top: 2rem;">
              <h2 id="formTitle" style="color:#064e3b; margin-bottom:1rem;">Tạo booking mới</h2>
              <label for="bookingCode">Mã booking</label>
              <input type="text" id="bookingCode" name="bookingCode" required placeholder="VD: BK001" pattern="^BK[0-9]{3,}$" title="Mã booking bắt đầu BK theo sau là số" />
              <label for="customer">Khách hàng</label>
              <input type="text" id="customer" name="customer" required placeholder="Tên khách hàng" />
              <label for="room">Phòng</label>
              <input type="text" id="room" name="room" required placeholder="Số phòng" pattern="^[0-9]+$" title="Chỉ nhập số phòng" />
              <label for="checkInDate">Ngày nhận</label>
              <input type="date" id="checkInDate" name="checkInDate" required />
              <label for="checkOutDate">Ngày trả</label>
              <input type="date" id="checkOutDate" name="checkOutDate" required />
              <label for="numberOfGuests">Số người</label>
              <input type="number" id="numberOfGuests" name="numberOfGuests" required min="1" max="20" />
              <label for="status">Trạng thái</label>
              <select id="status" name="status" required>
                <option value="" disabled selected>Chọn trạng thái</option>
                <option value="Confirmed">Đã xác nhận</option>
                <option value="Pending">Chờ xác nhận</option>
                <option value="CheckedIn">Đã nhận phòng</option>
                <option value="CheckedOut">Đã trả phòng</option>
                <option value="Cancelled">Đã hủy</option>
              </select>
              <div style="grid-column: 2 / 3; margin-top: 1.5rem;">
                <button type="submit" class="btn" id="submitBtn">Lưu booking</button>
                <button type="button" class="btn" id="cancelBtn" style="background:#ef4444; margin-left: 12px;">Hủy</button>
              </div>
            </form>
          `;
            setTimeout(setupBookingFormJS);
        }
    });
});
// Thiết lập sự kiện cho form booking khi nội dung được thay đổi
function setupBookingFormJS(){
    bookings = bookings || [];
    editingBookingId = null;
    const bookingTableBodyDyn = document.getElementById('bookingTableBody');
    const bookingFormDyn = document.getElementById('bookingForm');
    const formTitleDyn = document.getElementById('formTitle');
    const submitBtnDyn = document.getElementById('submitBtn');
    const cancelBtnDyn = document.getElementById('cancelBtn');
    const btnAddBookingDyn = document.getElementById('btnAddBooking');

    const inputBookingCodeDyn = document.getElementById('bookingCode');
    const inputCustomerDyn = document.getElementById('customer');
    const inputRoomDyn = document.getElementById('room');
    const inputCheckInDyn = document.getElementById('checkInDate');
    const inputCheckOutDyn = document.getElementById('checkOutDate');
    const inputGuestsDyn = document.getElementById('numberOfGuests');
    const selectStatusDyn = document.getElementById('status');

    function renderBookingTableDyn() {
        bookingTableBodyDyn.innerHTML = '';
        bookings.forEach((bk, index) => {
            const tr = document.createElement('tr');
            tr.tabIndex = 0;
            tr.innerHTML = `
            <td>${index + 1}</td>
            <td>${bk.code}</td>
            <td>${bk.customer}</td>
            <td>${bk.room}</td>
            <td>${bk.checkIn}</td>
            <td>${bk.checkOut}</td>
            <td>${bk.guests}</td>
            <td>${statusTextDyn(bk.status)}</td>
            <td>
              <button class="btn btnEdit" data-id="${bk.id}" style="padding: 6px 12px; font-size: 0.8rem;">Sửa</button>
              <button class="btn btnDelete" data-id="${bk.id}" style="padding: 6px 12px; font-size: 0.8rem; background:#ef4444; box-shadow:none;">Hủy</button>
            </td>`;
            bookingTableBodyDyn.appendChild(tr);
        });
        attachTableButtonEventsDyn();
    }
    function statusTextDyn(st){
        switch(st){
            case 'Confirmed': return 'Đã xác nhận';
            case 'Pending': return 'Chờ xác nhận';
            case 'CheckedIn': return 'Đã nhận phòng';
            case 'CheckedOut': return 'Đã trả phòng';
            case 'Cancelled': return 'Đã hủy';
            default: return st;
        }
    }
    function clearFormDyn() {
        bookingFormDyn.reset();
        editingBookingId = null;
        formTitleDyn.textContent = "Tạo booking mới";
        submitBtnDyn.textContent = "Lưu booking";
    }
    function showFormDyn(){
        bookingFormDyn.style.display = 'grid';
        btnAddBookingDyn.style.display = 'none';
        window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});
        inputBookingCodeDyn.focus();
    }
    function hideFormDyn(){
        bookingFormDyn.style.display = 'none';
        btnAddBookingDyn.style.display = 'inline-block';
        clearFormDyn();
    }
    function attachTableButtonEventsDyn(){
        document.querySelectorAll('.btnEdit').forEach(btn => {
            btn.onclick = e => {
                const id = Number(e.target.dataset.id);
                editBookingDyn(id);
            };
        });
        document.querySelectorAll('.btnDelete').forEach(btn => {
            btn.onclick = e => {
                const id = Number(e.target.dataset.id);
                cancelBookingDyn(id);
            };
        });
    }
    function editBookingDyn(id){
        const bk = bookings.find(b => b.id === id);
        if (!bk) return;
        editingBookingId = id;
        formTitleDyn.textContent = "Cập nhật booking";
        submitBtnDyn.textContent = "Cập nhật";
        inputBookingCodeDyn.value = bk.code;
        inputCustomerDyn.value = bk.customer;
        inputRoomDyn.value = bk.room;
        inputCheckInDyn.value = bk.checkIn;
        inputCheckOutDyn.value = bk.checkOut;
        inputGuestsDyn.value = bk.guests;
        selectStatusDyn.value = bk.status;
        showFormDyn();
    }
    function cancelBookingDyn(id){
        const bk = bookings.find(b => b.id === id);
        if(!bk) return;
        if(confirm(`Bạn có chắc chắn muốn hủy booking ${bk.code} của khách ${bk.customer}?`)){
            bk.status = 'Cancelled';
            renderBookingTableDyn();
        }
    }
    bookingFormDyn.onsubmit = e => {
        e.preventDefault();
        const code = inputBookingCodeDyn.value.trim();
        const customer = inputCustomerDyn.value.trim();
        const room = inputRoomDyn.value.trim();
        const checkIn = inputCheckInDyn.value;
        const checkOut = inputCheckOutDyn.value;
        const guests = Number(inputGuestsDyn.value);
        const status = selectStatusDyn.value;
        if(!code || !customer || !room || !checkIn || !checkOut || !guests || !status){
            alert('Vui lòng điền đầy đủ thông tin.');
            return;
        }
        if(new Date(checkOut) <= new Date(checkIn)){
            alert('Ngày trả phải lớn hơn ngày nhận.');
            return;
        }
        if(!editingBookingId && bookings.some(b => b.code === code)){
            alert('Mã booking đã tồn tại.');
            return;
        }
        if(editingBookingId){
            const idx = bookings.findIndex(b => b.id === editingBookingId);
            if(idx === -1) return;
            bookings[idx] = {id: editingBookingId, code, customer, room, checkIn, checkOut, guests, status};
            alert('Cập nhật booking thành công.');
        } else {
            const newId = bookings.length ? Math.max(...bookings.map(b => b.id)) +1 : 1;
            bookings.push({id: newId, code, customer, room, checkIn, checkOut, guests, status});
            alert('Tạo booking mới thành công.');
        }
        renderBookingTableDyn();
        hideFormDyn();
    };
    cancelBtnDyn.onclick = () => {
        hideFormDyn();
    };
    btnAddBookingDyn.onclick = () => {
        clearFormDyn();
        showFormDyn();
    };
    renderBookingTableDyn();
}

// Khởi tạo render bảng booking và form xử lý sự kiện cho tab mặc định
setupBookingFormJS();
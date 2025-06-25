let rooms = [
    {id: 1, roomNumber: "101", roomType: "Standard", floor: 1, view: "Pool", basePrice: 1200000, status: "Available"},
    {id: 2, roomNumber: "202", roomType: "Deluxe", floor: 2, view: "Sea", basePrice: 2500000, status: "Maintenance"},
];
let editingRoomId = null;

const roomTableBody = document.getElementById('roomTableBody');
const roomForm = document.getElementById('roomForm');
const formTitle = document.getElementById('formTitle');
const submitBtn = document.getElementById('submitBtn');
const cancelBtn = document.getElementById('cancelBtn');
const btnAddRoom = document.getElementById('btnAddRoom');

const inputRoomNumber = document.getElementById('roomNumber');
const selectRoomType = document.getElementById('roomType');
const inputFloor = document.getElementById('floor');
const selectView = document.getElementById('view');
const inputBasePrice = document.getElementById('basePrice');
const selectStatus = document.getElementById('status');

// Render table rows from rooms data
function renderRoomTable() {
    roomTableBody.innerHTML = '';
    rooms.forEach((room, index) => {
        const tr = document.createElement('tr');
        tr.tabIndex = 0;
        tr.innerHTML = `
          <td>${index + 1}</td>
          <td>${room.roomNumber}</td>
          <td>${room.roomType}</td>
          <td>${room.floor}</td>
          <td>${room.view}</td>
          <td>${room.basePrice.toLocaleString('vi-VN')} VNĐ</td>
          <td>${room.status === 'Available' ? 'Sẵn sàng' : (room.status === 'Maintenance' ? 'Bảo trì' : 'Dọn dẹp')}</td>
          <td>
            <button class="btn btnEdit" data-id="${room.id}" style="padding: 6px 12px; font-size: 0.8rem;">Sửa</button>
            <button class="btn btnDelete" data-id="${room.id}" style="padding: 6px 12px; font-size: 0.8rem; background:#ef4444; box-shadow:none;">Xóa</button>
          </td>
        `;
        roomTableBody.appendChild(tr);
    });
    attachTableButtonEvents();
}

// Clear form inputs
function clearForm() {
    roomForm.reset();
    editingRoomId = null;
    formTitle.textContent = "Thêm phòng mới";
    submitBtn.textContent = "Lưu phòng";
}

// Show form
function showForm() {
    roomForm.style.display = 'grid';
    btnAddRoom.style.display = 'none';
    window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});
    inputRoomNumber.focus();
}

// Hide form
function hideForm() {
    roomForm.style.display = 'none';
    btnAddRoom.style.display = 'inline-block';
    clearForm();
}

// Attach edit/delete button events
function attachTableButtonEvents() {
    document.querySelectorAll('.btnEdit').forEach(btn => {
        btn.onclick = e => {
            const id = Number(e.target.dataset.id);
            editRoom(id);
        };
    });
    document.querySelectorAll('.btnDelete').forEach(btn => {
        btn.onclick = e => {
            const id = Number(e.target.dataset.id);
            deleteRoom(id);
        };
    });
}

// Edit room by id
function editRoom(id) {
    const room = rooms.find(r => r.id === id);
    if (!room) return;
    editingRoomId = id;
    formTitle.textContent = "Cập nhật phòng";
    submitBtn.textContent = "Cập nhật";
    inputRoomNumber.value = room.roomNumber;
    selectRoomType.value = room.roomType;
    inputFloor.value = room.floor;
    selectView.value = room.view;
    inputBasePrice.value = room.basePrice;
    selectStatus.value = room.status;
    showForm();
}

// Delete room by id
function deleteRoom(id) {
    if (confirm('Bạn có chắc chắn muốn xóa phòng này?')) {
        rooms = rooms.filter(r => r.id !== id);
        renderRoomTable();
    }
}

// Add new or update existing room event handler
roomForm.onsubmit = e => {
    e.preventDefault();
    const roomNumber = inputRoomNumber.value.trim();
    const roomType = selectRoomType.value;
    const floor = Number(inputFloor.value);
    const view = selectView.value;
    const basePrice = Number(inputBasePrice.value);
    const status = selectStatus.value;

    if (!roomNumber || !roomType || !floor || !view || !basePrice || !status) {
        alert('Vui lòng điền đầy đủ thông tin phòng.');
        return;
    }

    // Check duplicate roomNumber for add new
    if (!editingRoomId && rooms.some(r => r.roomNumber === roomNumber)) {
        alert('Số phòng đã tồn tại.');
        return;
    }

    if (editingRoomId) {
        const index = rooms.findIndex(r => r.id === editingRoomId);
        if (index === -1) return;
        rooms[index] = {id: editingRoomId, roomNumber, roomType, floor, view, basePrice, status};
        alert('Cập nhật phòng thành công.');
    } else {
        // Auto increment id
        const newId = rooms.length ? Math.max(...rooms.map(r => r.id)) + 1 : 1;
        rooms.push({id: newId, roomNumber, roomType, floor, view, basePrice, status});
        alert('Thêm phòng mới thành công.');
    }
    renderRoomTable();
    hideForm();
};

cancelBtn.onclick = () => {
    hideForm();
};

btnAddRoom.onclick = () => {
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

        // For demo, only rooms section is fully implemented here
        if(section !== 'rooms'){
            mainHeading.textContent = link.textContent.trim();
            contentSection.innerHTML = '<p style="padding:1rem; color:#065f46; font-weight:600;">Chức năng ' + link.textContent.trim() + ' sẽ được phát triển tiếp theo.</p>';
        } else {
            mainHeading.textContent = 'Quản lý phòng';
            contentSection.innerHTML = `
          <button class="btn" id="btnAddRoom">Thêm phòng mới</button>
          <table aria-label="Danh sách phòng khách sạn" id="roomTable">
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
            <tbody id="roomTableBody"></tbody>
          </table>
          <form id="roomForm" style="display:none; margin-top: 2rem;">
            <h2 id="formTitle" style="color:#064e3b; margin-bottom:1rem;">Thêm phòng mới</h2>
            <label for="roomNumber">Số phòng</label>
            <input type="text" id="roomNumber" name="roomNumber" required placeholder="VD: 101" pattern="^[0-9]+$" title="Chỉ nhập số" />
            <label for="roomType">Loại phòng</label>
            <select id="roomType" name="roomType" required>
              <option value="" disabled selected>Chọn loại phòng</option>
              <option value="Standard">Standard</option>
              <option value="Deluxe">Deluxe</option>
              <option value="Suite">Suite</option>
            </select>
            <label for="floor">Tầng</label>
            <input type="number" id="floor" name="floor" required min="1" max="100" placeholder="1" />
            <label for="view">View</label>
            <select id="view" name="view" required>
              <option value="" disabled selected>Chọn view</option>
              <option value="City">Thành phố</option>
              <option value="Pool">Hồ bơi</option>
              <option value="Sea">Biển</option>
              <option value="Garden">Vườn</option>
            </select>
            <label for="basePrice">Giá cơ bản (VNĐ)</label>
            <input type="number" id="basePrice" name="basePrice" required min="0" step="1000" placeholder="1200000" />
            <label for="status">Trạng thái</label>
            <select id="status" name="status" required>
              <option value="" disabled selected>Chọn trạng thái</option>
              <option value="Available">Sẵn sàng</option>
              <option value="Maintenance">Bảo trì</option>
              <option value="Cleaning">Dọn dẹp</option>
            </select>
            <div style="grid-column: 2 / 3; margin-top: 1.5rem;">
              <button type="submit" class="btn" id="submitBtn">Lưu phòng</button>
              <button type="button" class="btn" id="cancelBtn" style="background:#ef4444; margin-left: 12px;">Hủy</button>
            </div>
          </form>`;
            setTimeout(setupRoomFormJS);
        }
    });
});

// Setup JS events for room form after injecting html dynamically
function setupRoomFormJS(){
    rooms = rooms || [];
    editingRoomId = null;
    // Cache DOM
    const roomTableBodyDyn = document.getElementById('roomTableBody');
    const roomFormDyn = document.getElementById('roomForm');
    const formTitleDyn = document.getElementById('formTitle');
    const submitBtnDyn = document.getElementById('submitBtn');
    const cancelBtnDyn = document.getElementById('cancelBtn');
    const btnAddRoomDyn = document.getElementById('btnAddRoom');

    const inputRoomNumberDyn = document.getElementById('roomNumber');
    const selectRoomTypeDyn = document.getElementById('roomType');
    const inputFloorDyn = document.getElementById('floor');
    const selectViewDyn = document.getElementById('view');
    const inputBasePriceDyn = document.getElementById('basePrice');
    const selectStatusDyn = document.getElementById('status');

    function renderRoomTableDyn() {
        roomTableBodyDyn.innerHTML = '';
        rooms.forEach((room, index) => {
            const tr = document.createElement('tr');
            tr.tabIndex = 0;
            tr.innerHTML = `
            <td>${index + 1}</td>
            <td>${room.roomNumber}</td>
            <td>${room.roomType}</td>
            <td>${room.floor}</td>
            <td>${room.view}</td>
            <td>${room.basePrice.toLocaleString('vi-VN')} VNĐ</td>
            <td>${room.status === 'Available' ? 'Sẵn sàng' : (room.status === 'Maintenance' ? 'Bảo trì' : 'Dọn dẹp')}</td>
            <td>
              <button class="btn btnEdit" data-id="${room.id}" style="padding: 6px 12px; font-size: 0.8rem;">Sửa</button>
              <button class="btn btnDelete" data-id="${room.id}" style="padding: 6px 12px; font-size: 0.8rem; background:#ef4444; box-shadow:none;">Xóa</button>
            </td>`;
            roomTableBodyDyn.appendChild(tr);
        });
        attachTableButtonEventsDyn();
    }
    function clearFormDyn() {
        roomFormDyn.reset();
        editingRoomId = null;
        formTitleDyn.textContent = "Thêm phòng mới";
        submitBtnDyn.textContent = "Lưu phòng";
    }
    function showFormDyn(){
        roomFormDyn.style.display = 'grid';
        btnAddRoomDyn.style.display = 'none';
        window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});
        inputRoomNumberDyn.focus();
    }
    function hideFormDyn(){
        roomFormDyn.style.display = 'none';
        btnAddRoomDyn.style.display = 'inline-block';
        clearFormDyn();
    }
    function attachTableButtonEventsDyn(){
        document.querySelectorAll('.btnEdit').forEach(btn => {
            btn.onclick = e => {
                const id = Number(e.target.dataset.id);
                editRoomDyn(id);
            };
        });
        document.querySelectorAll('.btnDelete').forEach(btn => {
            btn.onclick = e => {
                const id = Number(e.target.dataset.id);
                deleteRoomDyn(id);
            };
        });
    }
    function editRoomDyn(id){
        const room = rooms.find(r => r.id === id);
        if (!room) return;
        editingRoomId = id;
        formTitleDyn.textContent = "Cập nhật phòng";
        submitBtnDyn.textContent = "Cập nhật";
        inputRoomNumberDyn.value = room.roomNumber;
        selectRoomTypeDyn.value = room.roomType;
        inputFloorDyn.value = room.floor;
        selectViewDyn.value = room.view;
        inputBasePriceDyn.value = room.basePrice;
        selectStatusDyn.value = room.status;
        showFormDyn();
    }
    function deleteRoomDyn(id){
        if(confirm('Bạn có chắc chắn muốn xóa phòng này?')){
            rooms = rooms.filter(r => r.id !== id);
            renderRoomTableDyn();
        }
    }
    roomFormDyn.onsubmit = e => {
        e.preventDefault();
        const roomNumber = inputRoomNumberDyn.value.trim();
        const roomType = selectRoomTypeDyn.value;
        const floor = Number(inputFloorDyn.value);
        const view = selectViewDyn.value;
        const basePrice = Number(inputBasePriceDyn.value);
        const status = selectStatusDyn.value;
        if (!roomNumber || !roomType || !floor || !view || !basePrice || !status) {
            alert('Vui lòng điền đầy đủ thông tin phòng.');
            return;
        }
        if(!editingRoomId && rooms.some(r => r.roomNumber === roomNumber)){
            alert('Số phòng đã tồn tại.');
            return;
        }
        if(editingRoomId){
            const index = rooms.findIndex(r => r.id === editingRoomId);
            if(index === -1) return;
            rooms[index] = {id: editingRoomId, roomNumber, roomType, floor, view, basePrice, status};
            alert('Cập nhật phòng thành công.');
        } else {
            const newId = rooms.length ? Math.max(...rooms.map(r => r.id)) + 1 : 1;
            rooms.push({id: newId, roomNumber, roomType, floor, view, basePrice, status});
            alert('Thêm phòng mới thành công.');
        }
        renderRoomTableDyn();
        hideFormDyn();
    };
    cancelBtnDyn.onclick = () => {
        hideFormDyn();
    };
    btnAddRoomDyn.onclick = () => {
        clearFormDyn();
        showFormDyn();
    };
    renderRoomTableDyn();
}

// Khởi tạo render và xử lý sự kiện form cho tab Quản lý phòng mặc định
setupRoomFormJS();
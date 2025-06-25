let staffList = [
    {id: 1, name: "Nguyễn Văn A", email: "vana@example.com", role: "Quản lý", permissions: "Quản lý đặt phòng, Quản lý thanh toán", status: "Active"},
    {id: 2, name: "Trần Thị B", email: "thib@example.com", role: "Lễ tân", permissions: "Xác nhận booking, Đón tiếp khách", status: "Active"},
];
let editingStaffId = null;

const staffTableBody = document.getElementById('staffTableBody');
const staffForm = document.getElementById('staffForm');
const formTitle = document.getElementById('formTitle');
const submitBtn = document.getElementById('submitBtn');
const cancelBtn = document.getElementById('cancelBtn');
const btnAddStaff = document.getElementById('btnAddStaff');

const inputStaffId = document.getElementById('staffId');
const inputStaffName = document.getElementById('staffName');
const inputStaffEmail = document.getElementById('staffEmail');
const inputStaffRole = document.getElementById('staffRole');
const textareaStaffPermissions = document.getElementById('staffPermissions');
const selectStaffStatus = document.getElementById('staffStatus');

function renderStaffTable() {
    staffTableBody.innerHTML = '';
    staffList.forEach((staff, index) => {
        const tr = document.createElement('tr');
        tr.tabIndex = 0;
        tr.innerHTML = `
          <td>${index + 1}</td>
          <td>${staff.id}</td>
          <td>${staff.name}</td>
          <td>${staff.email}</td>
          <td>${staff.role}</td>
          <td title="${staff.permissions}">${staff.permissions.length > 30 ? staff.permissions.slice(0, 30) + "..." : staff.permissions}</td>
          <td>${staff.status === "Active" ? "Hoạt động" : "Vô hiệu hóa"}</td>
          <td>
            <button class="btn btnEdit" data-id="${staff.id}" style="padding: 6px 12px; font-size: 0.8rem;">Sửa</button>
            <button class="btn btnDeactivate" data-id="${staff.id}" style="padding: 6px 12px; font-size: 0.8rem; background:#ef4444; box-shadow:none;">
              ${staff.status === "Active" ? "Vô hiệu hóa" : "Kích hoạt"}
            </button>
          </td>
        `;
        staffTableBody.appendChild(tr);
    });
    attachTableButtonEvents();
}

function clearForm() {
    staffForm.reset();
    inputStaffId.value = '';
    editingStaffId = null;
    formTitle.textContent = "Thêm nhân viên mới";
    submitBtn.textContent = "Lưu nhân viên";
    selectStaffStatus.value = "Active";
}

function showForm() {
    staffForm.style.display = 'grid';
    btnAddStaff.style.display = 'none';
    window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});
    inputStaffName.focus();
}

function hideForm() {
    staffForm.style.display = 'none';
    btnAddStaff.style.display = 'inline-block';
    clearForm();
}

function attachTableButtonEvents() {
    document.querySelectorAll('.btnEdit').forEach(btn => {
        btn.onclick = e => {
            const id = Number(e.target.dataset.id);
            editStaff(id);
        };
    });
    document.querySelectorAll('.btnDeactivate').forEach(btn => {
        btn.onclick = e => {
            const id = Number(e.target.dataset.id);
            toggleStaffStatus(id);
        };
    });
}

function editStaff(id) {
    const staff = staffList.find(s => s.id === id);
    if (!staff) return;
    editingStaffId = id;
    formTitle.textContent = "Cập nhật nhân viên";
    submitBtn.textContent = "Cập nhật";
    inputStaffId.value = staff.id;
    inputStaffName.value = staff.name;
    inputStaffEmail.value = staff.email;
    inputStaffRole.value = staff.role;
    textareaStaffPermissions.value = staff.permissions;
    selectStaffStatus.value = staff.status;
    showForm();
}

function toggleStaffStatus(id) {
    const staff = staffList.find(s => s.id === id);
    if (!staff) return;
    const action = staff.status === "Active" ? "vô hiệu hóa" : "kích hoạt";
    if (confirm(`Bạn có chắc chắn muốn ${action} tài khoản nhân viên ${staff.name}?`)) {
        staff.status = staff.status === "Active" ? "Deactivated" : "Active";
        renderStaffTable();
    }
}

staffForm.onsubmit = e => {
    e.preventDefault();
    const name = inputStaffName.value.trim();
    const email = inputStaffEmail.value.trim();
    const role = inputStaffRole.value.trim();
    const permissions = textareaStaffPermissions.value.trim();
    const status = selectStaffStatus.value;
    if (!name || !email || !role || !permissions || !status) {
        alert('Vui lòng điền đầy đủ thông tin nhân viên.');
        return;
    }
    if (editingStaffId) {
        const idx = staffList.findIndex(s => s.id === editingStaffId);
        if (idx === -1) return;
        staffList[idx] = { id: editingStaffId, name, email, role, permissions, status };
        alert('Cập nhật nhân viên thành công.');
    } else {
        const newId = staffList.length ? Math.max(...staffList.map(s => s.id)) + 1 : 1;
        staffList.push({ id: newId, name, email, role, permissions, status });
        alert('Thêm nhân viên mới thành công.');
    }
    renderStaffTable();
    hideForm();
};

cancelBtn.onclick = () => {
    hideForm();
};

btnAddStaff.onclick = () => {
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

        if (section !== 'staff') {
            mainHeading.textContent = link.textContent.trim();
            contentSection.innerHTML = '<p style="padding:1rem; color:#065f46; font-weight:600;">Chức năng ' + link.textContent.trim() + ' sẽ được phát triển tiếp theo.</p>';
        } else {
            mainHeading.textContent = 'Quản lý nhân viên';
            contentSection.innerHTML = `
            <button class="btn" id="btnAddStaff">Thêm nhân viên mới</button>
            <table aria-label="Danh sách nhân viên" id="staffTable">
              <thead>
                <tr>
                  <th>STT</th>
                  <th>ID</th>
                  <th>Tên</th>
                  <th>Email</th>
                  <th>Chức vụ</th>
                  <th>Quyền hạn</th>
                  <th>Trạng thái</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody id="staffTableBody"></tbody>
            </table>
            <form id="staffForm" style="display:none; margin-top: 2rem;">
              <h2 id="formTitle" style="color:#064e3b; margin-bottom:1rem;">Thêm nhân viên mới</h2>
              <label for="staffId">ID</label>
              <input type="text" id="staffId" name="staffId" readonly placeholder="Tự động tạo" disabled />
              <label for="staffName">Tên</label>
              <input type="text" id="staffName" name="staffName" required placeholder="Nhập tên nhân viên" />
              <label for="staffEmail">Email</label>
              <input type="email" id="staffEmail" name="staffEmail" required placeholder="email@example.com" />
              <label for="staffRole">Chức vụ</label>
              <input type="text" id="staffRole" name="staffRole" required placeholder="VD: Quản lý, Lễ tân" />
              <label for="staffPermissions">Quyền hạn</label>
              <textarea id="staffPermissions" name="staffPermissions" placeholder="Mô tả quyền hạn"></textarea>
              <label for="staffStatus">Trạng thái</label>
              <select id="staffStatus" name="staffStatus" required>
                <option value="Active" selected>Hoạt động</option>
                <option value="Deactivated">Vô hiệu hóa</option>
              </select>
              <div style="grid-column: 2 / 3; margin-top: 1.5rem;">
                <button type="submit" class="btn" id="submitBtn">Lưu nhân viên</button>
                <button type="button" class="btn" id="cancelBtn" style="background:#ef4444; margin-left: 12px;">Hủy</button>
              </div>
            </form>
          `;
            setTimeout(setupStaffFormJS);
        }
    });
});

function setupStaffFormJS() {
    staffList = staffList || [];
    editingStaffId = null;
    const staffTableBodyDyn = document.getElementById('staffTableBody');
    const staffFormDyn = document.getElementById('staffForm');
    const formTitleDyn = document.getElementById('formTitle');
    const submitBtnDyn = document.getElementById('submitBtn');
    const cancelBtnDyn = document.getElementById('cancelBtn');
    const btnAddStaffDyn = document.getElementById('btnAddStaff');

    const inputStaffIdDyn = document.getElementById('staffId');
    const inputStaffNameDyn = document.getElementById('staffName');
    const inputStaffEmailDyn = document.getElementById('staffEmail');
    const inputStaffRoleDyn = document.getElementById('staffRole');
    const textareaStaffPermissionsDyn = document.getElementById('staffPermissions');
    const selectStaffStatusDyn = document.getElementById('staffStatus');

    function renderStaffTableDyn() {
        staffTableBodyDyn.innerHTML = '';
        staffList.forEach((staff, index) => {
            const tr = document.createElement('tr');
            tr.tabIndex = 0;
            tr.innerHTML = `
            <td>${index + 1}</td>
            <td>${staff.id}</td>
            <td>${staff.name}</td>
            <td>${staff.email}</td>
            <td>${staff.role}</td>
            <td title="${staff.permissions}">${staff.permissions.length > 30 ? staff.permissions.slice(0, 30) + '...' : staff.permissions}</td>
            <td>${staff.status === "Active" ? "Hoạt động" : "Vô hiệu hóa"}</td>
            <td>
              <button class="btn btnEdit" data-id="${staff.id}" style="padding: 6px 12px; font-size: 0.8rem;">Sửa</button>
              <button class="btn btnDeactivate" data-id="${staff.id}" style="padding: 6px 12px; font-size: 0.8rem; background:#ef4444; box-shadow:none;">
                ${staff.status === "Active" ? "Vô hiệu hóa" : "Kích hoạt"}
              </button>
            </td>
          `;
            staffTableBodyDyn.appendChild(tr);
        });
        attachTableButtonEventsDyn();
    }

    function clearFormDyn() {
        staffFormDyn.reset();
        inputStaffIdDyn.value = '';
        editingStaffId = null;
        formTitleDyn.textContent = "Thêm nhân viên mới";
        submitBtnDyn.textContent = "Lưu nhân viên";
        selectStaffStatusDyn.value = "Active";
    }

    function showFormDyn() {
        staffFormDyn.style.display = 'grid';
        btnAddStaffDyn.style.display = 'none';
        window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});
        inputStaffNameDyn.focus();
    }

    function hideFormDyn() {
        staffFormDyn.style.display = 'none';
        btnAddStaffDyn.style.display = 'inline-block';
        clearFormDyn();
    }

    function attachTableButtonEventsDyn() {
        document.querySelectorAll('.btnEdit').forEach(btn => {
            btn.onclick = e => {
                const id = Number(e.target.dataset.id);
                editStaffDyn(id);
            };
        });
        document.querySelectorAll('.btnDeactivate').forEach(btn => {
            btn.onclick = e => {
                const id = Number(e.target.dataset.id);
                toggleStaffStatusDyn(id);
            };
        });
    }

    function editStaffDyn(id) {
        const staff = staffList.find(s => s.id === id);
        if (!staff) return;
        editingStaffId = id;
        formTitleDyn.textContent = "Cập nhật nhân viên";
        submitBtnDyn.textContent = "Cập nhật";
        inputStaffIdDyn.value = staff.id;
        inputStaffNameDyn.value = staff.name;
        inputStaffEmailDyn.value = staff.email;
        inputStaffRoleDyn.value = staff.role;
        textareaStaffPermissionsDyn.value = staff.permissions;
        selectStaffStatusDyn.value = staff.status;
        showFormDyn();
    }

    function toggleStaffStatusDyn(id) {
        const staff = staffList.find(s => s.id === id);
        if (!staff) return;
        const action = staff.status === "Active" ? "vô hiệu hóa" : "kích hoạt";
        if (confirm(`Bạn có chắc chắn muốn ${action} tài khoản nhân viên ${staff.name}?`)) {
            staff.status = staff.status === "Active" ? "Deactivated" : "Active";
            renderStaffTableDyn();
        }
    }

    staffFormDyn.onsubmit = e => {
        e.preventDefault();
        const name = inputStaffNameDyn.value.trim();
        const email = inputStaffEmailDyn.value.trim();
        const role = inputStaffRoleDyn.value.trim();
        const permissions = textareaStaffPermissionsDyn.value.trim();
        const status = selectStaffStatusDyn.value;
        if (!name || !email || !role || !permissions || !status) {
            alert('Vui lòng điền đầy đủ thông tin nhân viên.');
            return;
        }
        if (editingStaffId) {
            const idx = staffList.findIndex(s => s.id === editingStaffId);
            if (idx === -1) return;
            staffList[idx] = { id: editingStaffId, name, email, role, permissions, status };
            alert('Cập nhật nhân viên thành công.');
        } else {
            const newId = staffList.length ? Math.max(...staffList.map(s => s.id)) + 1 : 1;
            staffList.push({ id: newId, name, email, role, permissions, status });
            alert('Thêm nhân viên mới thành công.');
        }
        renderStaffTableDyn();
        hideFormDyn();
    };

    cancelBtnDyn.onclick = () => {
        hideFormDyn();
    };

    btnAddStaffDyn.onclick = () => {
        clearFormDyn();
        showFormDyn();
    };

    renderStaffTableDyn();
}
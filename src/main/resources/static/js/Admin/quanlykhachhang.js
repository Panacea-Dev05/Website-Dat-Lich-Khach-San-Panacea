let customers = [
    {id: 1, name: "Nguyễn Văn A", email: "vana@example.com", phone: "0903123456", address: "123 Đường Lê Lợi, TP.HCM", status: "Active"},
    {id: 2, name: "Trần Thị B", email: "thib@example.com", phone: "0912345678", address: "456 Đường Trần Hưng Đạo, Đà Nẵng", status: "Locked"},
];
let editingCustomerId = null;

const customerTableBody = document.getElementById('customerTableBody');
const customerForm = document.getElementById('customerForm');
const formTitle = document.getElementById('formTitle');
const submitBtn = document.getElementById('submitBtn');
const cancelBtn = document.getElementById('cancelBtn');
const btnAddCustomer = document.getElementById('btnAddCustomer');

const inputCustomerId = document.getElementById('customerId');
const inputCustomerName = document.getElementById('customerName');
const inputCustomerEmail = document.getElementById('customerEmail');
const inputCustomerPhone = document.getElementById('customerPhone');
const inputCustomerAddress = document.getElementById('customerAddress');
const selectCustomerStatus = document.getElementById('customerStatus');

function renderCustomerTable() {
    customerTableBody.innerHTML = '';
    customers.forEach((cus, index) => {
        const tr = document.createElement('tr');
        tr.tabIndex = 0;
        tr.innerHTML = `
          <td>${index + 1}</td>
          <td>${cus.id}</td>
          <td>${cus.name}</td>
          <td>${cus.email}</td>
          <td>${cus.phone}</td>
          <td>${cus.address}</td>
          <td>${cus.status === 'Active' ? 'Hoạt động' : 'Khóa'}</td>
          <td>
            <button class="btn btnEdit" data-id="${cus.id}" style="padding: 6px 12px; font-size: 0.8rem;">Sửa</button>
            <button class="btn btnLock" data-id="${cus.id}" style="padding: 6px 12px; font-size: 0.8rem; background:#ef4444; box-shadow:none;">${cus.status === 'Active' ? 'Khóa' : 'Mở khóa'}</button>
          </td>
        `;
        customerTableBody.appendChild(tr);
    });
    attachTableButtonEvents();
}
function clearForm() {
    customerForm.reset();
    inputCustomerId.value = '';
    editingCustomerId = null;
    formTitle.textContent = "Thêm khách hàng mới";
    submitBtn.textContent = "Lưu khách hàng";
    selectCustomerStatus.value = 'Active';
}
function showForm() {
    customerForm.style.display = 'grid';
    btnAddCustomer.style.display = 'none';
    window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});
    inputCustomerName.focus();
}
function hideForm() {
    customerForm.style.display = 'none';
    btnAddCustomer.style.display = 'inline-block';
    clearForm();
}
function attachTableButtonEvents() {
    document.querySelectorAll('.btnEdit').forEach(btn => {
        btn.onclick = e => {
            const id = Number(e.target.dataset.id);
            editCustomer(id);
        };
    });
    document.querySelectorAll('.btnLock').forEach(btn => {
        btn.onclick = e => {
            const id = Number(e.target.dataset.id);
            toggleLockCustomer(id);
        };
    });
}
function editCustomer(id) {
    const cus = customers.find(c => c.id === id);
    if (!cus) return;
    editingCustomerId = id;
    formTitle.textContent = "Cập nhật khách hàng";
    submitBtn.textContent = "Cập nhật";
    inputCustomerId.value = cus.id;
    inputCustomerName.value = cus.name;
    inputCustomerEmail.value = cus.email;
    inputCustomerPhone.value = cus.phone;
    inputCustomerAddress.value = cus.address;
    selectCustomerStatus.value = cus.status;
    showForm();
}
function toggleLockCustomer(id) {
    const cus = customers.find(c => c.id === id);
    if(!cus) return;
    const action = cus.status === 'Active' ? 'khóa' : 'mở khóa';
    if(confirm(`Bạn có chắc chắn muốn ${action} tài khoản của khách hàng ${cus.name}?`)){
        cus.status = cus.status === 'Active' ? 'Locked' : 'Active';
        renderCustomerTable();
    }
}
customerForm.onsubmit = e => {
    e.preventDefault();
    const name = inputCustomerName.value.trim();
    const email = inputCustomerEmail.value.trim();
    const phone = inputCustomerPhone.value.trim();
    const address = inputCustomerAddress.value.trim();
    const status = selectCustomerStatus.value;
    if(!name || !email || !phone || !address || !status){
        alert('Vui lòng điền đầy đủ thông tin khách hàng.');
        return;
    }
    if(editingCustomerId){
        const idx = customers.findIndex(c => c.id === editingCustomerId);
        if(idx === -1) return;
        customers[idx] = {id: editingCustomerId, name, email, phone, address, status};
        alert('Cập nhật khách hàng thành công.');
    } else {
        const newId = customers.length ? Math.max(...customers.map(c => c.id)) + 1 : 1;
        customers.push({id: newId, name, email, phone, address, status});
        alert('Thêm khách hàng mới thành công.');
    }
    renderCustomerTable();
    hideForm();
};
cancelBtn.onclick = () => {
    hideForm();
};
btnAddCustomer.onclick = () => {
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

        if(section !== 'customers'){
            mainHeading.textContent = link.textContent.trim();
            contentSection.innerHTML = '<p style="padding:1rem; color:#065f46; font-weight:600;">Chức năng ' + link.textContent.trim() + ' sẽ được phát triển tiếp theo.</p>';
        } else {
            mainHeading.textContent = 'Quản lý khách hàng';
            contentSection.innerHTML = `
            <button class="btn" id="btnAddCustomer">Thêm khách hàng mới</button>
            <table aria-label="Danh sách khách hàng" id="customerTable">
              <thead>
                <tr>
                  <th>STT</th>
                  <th>ID</th>
                  <th>Tên</th>
                  <th>Email</th>
                  <th>Số điện thoại</th>
                  <th>Địa chỉ</th>
                  <th>Trạng thái</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody id="customerTableBody"></tbody>
            </table>
            <form id="customerForm" style="display:none; margin-top: 2rem;">
              <h2 id="formTitle" style="color:#064e3b; margin-bottom:1rem;">Thêm khách hàng mới</h2>
              <label for="customerId">ID</label>
              <input type="text" id="customerId" name="customerId" readonly placeholder="Tự động tạo" disabled />
              <label for="customerName">Tên</label>
              <input type="text" id="customerName" name="customerName" required placeholder="Nhập tên khách hàng" />
              <label for="customerEmail">Email</label>
              <input type="email" id="customerEmail" name="customerEmail" required placeholder="abc@example.com" />
              <label for="customerPhone">Số điện thoại</label>
              <input type="text" id="customerPhone" name="customerPhone" required placeholder="0903123456" pattern="^[0-9()+ -]{7,15}$" title="Chỉ nhập số, dấu + - ( )" />
              <label for="customerAddress">Địa chỉ</label>
              <textarea id="customerAddress" name="customerAddress" placeholder="Nhập địa chỉ"></textarea>
              <label for="customerStatus">Trạng thái</label>
              <select id="customerStatus" name="customerStatus" required>
                <option value="Active" selected>Hoạt động</option>
                <option value="Locked">Khóa</option>
              </select>
              <div style="grid-column: 2 / 3; margin-top: 1.5rem;">
                <button type="submit" class="btn" id="submitBtn">Lưu khách hàng</button>
                <button type="button" class="btn" id="cancelBtn" style="background:#ef4444; margin-left: 12px;">Hủy</button>
              </div>
            </form>
          `;
            setTimeout(setupCustomerFormJS);
        }
    });
});
// Setup JS form events when content is dynamically injected
function setupCustomerFormJS(){
    customers = customers || [];
    editingCustomerId = null;
    const customerTableBodyDyn = document.getElementById('customerTableBody');
    const customerFormDyn = document.getElementById('customerForm');
    const formTitleDyn = document.getElementById('formTitle');
    const submitBtnDyn = document.getElementById('submitBtn');
    const cancelBtnDyn = document.getElementById('cancelBtn');
    const btnAddCustomerDyn = document.getElementById('btnAddCustomer');

    const inputCustomerIdDyn = document.getElementById('customerId');
    const inputCustomerNameDyn = document.getElementById('customerName');
    const inputCustomerEmailDyn = document.getElementById('customerEmail');
    const inputCustomerPhoneDyn = document.getElementById('customerPhone');
    const inputCustomerAddressDyn = document.getElementById('customerAddress');
    const selectCustomerStatusDyn = document.getElementById('customerStatus');

    function renderCustomerTableDyn() {
        customerTableBodyDyn.innerHTML = '';
        customers.forEach((cus, index) => {
            const tr = document.createElement('tr');
            tr.tabIndex = 0;
            tr.innerHTML = `
            <td>${index + 1}</td>
            <td>${cus.id}</td>
            <td>${cus.name}</td>
            <td>${cus.email}</td>
            <td>${cus.phone}</td>
            <td>${cus.address}</td>
            <td>${cus.status === 'Active' ? 'Hoạt động' : 'Khóa'}</td>
            <td>
              <button class="btn btnEdit" data-id="${cus.id}" style="padding: 6px 12px; font-size: 0.8rem;">Sửa</button>
              <button class="btn btnLock" data-id="${cus.id}" style="padding: 6px 12px; font-size: 0.8rem; background:#ef4444; box-shadow:none;">
                ${cus.status === 'Active' ? 'Khóa' : 'Mở khóa'}
              </button>
            </td>
          `;
            customerTableBodyDyn.appendChild(tr);
        });
        attachTableButtonEventsDyn();
    }
    function clearFormDyn() {
        customerFormDyn.reset();
        inputCustomerIdDyn.value = '';
        editingCustomerId = null;
        formTitleDyn.textContent = "Thêm khách hàng mới";
        submitBtnDyn.textContent = "Lưu khách hàng";
        selectCustomerStatusDyn.value = 'Active';
    }
    function showFormDyn(){
        customerFormDyn.style.display = 'grid';
        btnAddCustomerDyn.style.display = 'none';
        window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});
        inputCustomerNameDyn.focus();
    }
    function hideFormDyn(){
        customerFormDyn.style.display = 'none';
        btnAddCustomerDyn.style.display = 'inline-block';
        clearFormDyn();
    }
    function attachTableButtonEventsDyn(){
        document.querySelectorAll('.btnEdit').forEach(btn => {
            btn.onclick = e => {
                const id = Number(e.target.dataset.id);
                editCustomerDyn(id);
            };
        });
        document.querySelectorAll('.btnLock').forEach(btn => {
            btn.onclick = e => {
                const id = Number(e.target.dataset.id);
                toggleLockCustomerDyn(id);
            };
        });
    }
    function editCustomerDyn(id) {
        const cus = customers.find(c => c.id === id);
        if(!cus) return;
        editingCustomerId = id;
        formTitleDyn.textContent = "Cập nhật khách hàng";
        submitBtnDyn.textContent = "Cập nhật";
        inputCustomerIdDyn.value = cus.id;
        inputCustomerNameDyn.value = cus.name;
        inputCustomerEmailDyn.value = cus.email;
        inputCustomerPhoneDyn.value = cus.phone;
        inputCustomerAddressDyn.value = cus.address;
        selectCustomerStatusDyn.value = cus.status;
        showFormDyn();
    }
    function toggleLockCustomerDyn(id) {
        const cus = customers.find(c => c.id === id);
        if(!cus) return;
        const action = cus.status === 'Active' ? 'khóa' : 'mở khóa';
        if(confirm(`Bạn có chắc chắn muốn ${action} tài khoản của khách hàng ${cus.name}?`)){
            cus.status = cus.status === 'Active' ? 'Locked' : 'Active';
            renderCustomerTableDyn();
        }
    }
    customerFormDyn.onsubmit = e => {
        e.preventDefault();
        const name = inputCustomerNameDyn.value.trim();
        const email = inputCustomerEmailDyn.value.trim();
        const phone = inputCustomerPhoneDyn.value.trim();
        const address = inputCustomerAddressDyn.value.trim();
        const status = selectCustomerStatusDyn.value;
        if(!name || !email || !phone || !address || !status){
            alert('Vui lòng điền đầy đủ thông tin khách hàng.');
            return;
        }
        if(editingCustomerId){
            const idx = customers.findIndex(c => c.id === editingCustomerId);
            if(idx === -1) return;
            customers[idx] = {id: editingCustomerId, name, email, phone, address, status};
            alert('Cập nhật khách hàng thành công.');
        } else {
            const newId = customers.length ? Math.max(...customers.map(c => c.id)) + 1 : 1;
            customers.push({id: newId, name, email, phone, address, status});
            alert('Thêm khách hàng mới thành công.');
        }
        renderCustomerTableDyn();
        hideFormDyn();
    };
    cancelBtnDyn.onclick = () => {
        hideFormDyn();
    };
    btnAddCustomerDyn.onclick = () => {
        clearFormDyn();
        showFormDyn();
    };
    renderCustomerTableDyn();
}

// Sidebar toggle for mobile
const mobileToggle = document.getElementById('mobileToggle');
const sidebar = document.getElementById('sidebar');
mobileToggle.addEventListener('click', () => {
    sidebar.classList.toggle('open');
});
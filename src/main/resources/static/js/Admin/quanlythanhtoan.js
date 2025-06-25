let payments = [
    {id: 1, code: "TN001", bookingCode: "BK001", date: "2024-07-02", amount: 4800000, method: "Online", status: "Completed"},
    {id: 2, code: "TN002", bookingCode: "BK002", date: "2024-07-04", amount: 7500000, method: "Cash", status: "Pending"},
];
let editingPaymentId = null;

const paymentTableBody = document.getElementById('paymentTableBody');
const paymentForm = document.getElementById('paymentForm');
const formTitle = document.getElementById('formTitle');
const submitBtn = document.getElementById('submitBtn');
const cancelBtn = document.getElementById('cancelBtn');
const btnAddPayment = document.getElementById('btnAddPayment');

const inputPaymentCode = document.getElementById('paymentCode');
const inputBookingCode = document.getElementById('bookingCode');
const inputPaymentDate = document.getElementById('paymentDate');
const inputAmount = document.getElementById('amount');
const selectMethod = document.getElementById('method');
const selectStatus = document.getElementById('status');

function renderPaymentTable() {
    paymentTableBody.innerHTML = '';
    payments.forEach((pay, index) => {
        const tr = document.createElement('tr');
        tr.tabIndex = 0;
        tr.innerHTML = `
          <td>${index + 1}</td>
          <td>${pay.code}</td>
          <td>${pay.bookingCode}</td>
          <td>${pay.date}</td>
          <td>${pay.amount.toLocaleString('vi-VN')} VNĐ</td>
          <td>${pay.method}</td>
          <td>${statusText(pay.status)}</td>
          <td>
            <button class="btn btnEdit" data-id="${pay.id}" style="padding: 6px 12px; font-size: 0.8rem;">Sửa</button>
            <button class="btn btnDelete" data-id="${pay.id}" style="padding: 6px 12px; font-size: 0.8rem; background:#ef4444; box-shadow:none;">Xóa</button>
          </td>
        `;
        paymentTableBody.appendChild(tr);
    });
    attachTableButtonEvents();
}
function statusText(st){
    switch(st){
        case 'Completed': return 'Hoàn thành';
        case 'Pending': return 'Chờ xử lý';
        case 'Failed': return 'Thất bại';
        case 'Refunded': return 'Hoàn tiền';
        default: return st;
    }
}
function clearForm() {
    paymentForm.reset();
    editingPaymentId = null;
    formTitle.textContent = "Tạo thanh toán mới";
    submitBtn.textContent = "Lưu thanh toán";
}
function showForm() {
    paymentForm.style.display = 'grid';
    btnAddPayment.style.display = 'none';
    window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});
    inputPaymentCode.focus();
}
function hideForm() {
    paymentForm.style.display = 'none';
    btnAddPayment.style.display = 'inline-block';
    clearForm();
}
function attachTableButtonEvents() {
    document.querySelectorAll('.btnEdit').forEach(btn => {
        btn.onclick = e => {
            const id = Number(e.target.dataset.id);
            editPayment(id);
        };
    });
    document.querySelectorAll('.btnDelete').forEach(btn => {
        btn.onclick = e => {
            const id = Number(e.target.dataset.id);
            deletePayment(id);
        };
    });
}
function editPayment(id) {
    const payment = payments.find(p => p.id === id);
    if (!payment) return;
    editingPaymentId = id;
    formTitle.textContent = "Cập nhật thanh toán";
    submitBtn.textContent = "Cập nhật";
    inputPaymentCode.value = payment.code;
    inputBookingCode.value = payment.bookingCode;
    inputPaymentDate.value = payment.date;
    inputAmount.value = payment.amount;
    selectMethod.value = payment.method;
    selectStatus.value = payment.status;
    showForm();
}
function deletePayment(id) {
    if (confirm('Bạn có chắc chắn muốn xóa thanh toán này?')) {
        payments = payments.filter(p => p.id !== id);
        renderPaymentTable();
    }
}
paymentForm.onsubmit = e => {
    e.preventDefault();
    const code = inputPaymentCode.value.trim();
    const bookingCodeValue = inputBookingCode.value.trim();
    const date = inputPaymentDate.value;
    const amount = Number(inputAmount.value);
    const method = selectMethod.value;
    const status = selectStatus.value;
    if (!code || !bookingCodeValue || !date || !amount || !method || !status) {
        alert('Vui lòng điền đầy đủ thông tin.');
        return;
    }
    if (amount <= 0) {
        alert('Số tiền phải lớn hơn 0.');
        return;
    }
    if (!editingPaymentId && payments.some(p => p.code === code)) {
        alert('Mã thanh toán đã tồn tại.');
        return;
    }
    if (editingPaymentId) {
        const idx = payments.findIndex(p => p.id === editingPaymentId);
        if (idx === -1) return;
        payments[idx] = { id: editingPaymentId, code, bookingCode: bookingCodeValue, date, amount, method, status };
        alert('Cập nhật thanh toán thành công.');
    } else {
        const newId = payments.length ? Math.max(...payments.map(p => p.id)) + 1 : 1;
        payments.push({ id: newId, code, bookingCode: bookingCodeValue, date, amount, method, status });
        alert('Tạo thanh toán mới thành công.');
    }
    renderPaymentTable();
    hideForm();
};
cancelBtn.onclick = () => {
    hideForm();
};
btnAddPayment.onclick = () => {
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

        if (section !== 'payments') {
            mainHeading.textContent = link.textContent.trim();
            contentSection.innerHTML = '<p style="padding:1rem; color:#065f46; font-weight:600;">Chức năng ' + link.textContent.trim() + ' sẽ được phát triển tiếp theo.</p>';
        } else {
            mainHeading.textContent = 'Quản lý thanh toán';
            contentSection.innerHTML = `
            <button class="btn" id="btnAddPayment">Tạo thanh toán mới</button>
            <table aria-label="Danh sách thanh toán" id="paymentTable">
              <thead>
                <tr>
                  <th>STT</th>
                  <th>Mã thanh toán</th>
                  <th>Mã booking</th>
                  <th>Ngày</th>
                  <th>Số tiền (VNĐ)</th>
                  <th>Phương thức</th>
                  <th>Trạng thái</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody id="paymentTableBody"></tbody>
            </table>
            <form id="paymentForm" style="display:none; margin-top: 2rem;">
              <h2 id="formTitle" style="color:#064e3b; margin-bottom:1rem;">Tạo thanh toán mới</h2>
              <label for="paymentCode">Mã thanh toán</label>
              <input type="text" id="paymentCode" name="paymentCode" required placeholder="TN001" pattern="^(TN)[0-9]{3,}$" title="Mã bắt đầu TN theo sau là số" />
              <label for="bookingCode">Mã booking</label>
              <input type="text" id="bookingCode" name="bookingCode" required placeholder="BK001" pattern="^(BK)[0-9]{3,}$" title="Mã booking bắt đầu BK theo sau là số" />
              <label for="paymentDate">Ngày thanh toán</label>
              <input type="date" id="paymentDate" name="paymentDate" required />
              <label for="amount">Số tiền (VNĐ)</label>
              <input type="number" id="amount" name="amount" required min="0" step="1000" />
              <label for="method">Phương thức</label>
              <select id="method" name="method" required>
                <option value="" disabled selected>Chọn phương thức</option>
                <option value="Online">Thanh toán Online</option>
                <option value="Cash">Tiền mặt</option>
                <option value="VNPay">VNPay</option>
                <option value="MoMo">MoMo</option>
                <option value="Stripe">Stripe</option>
              </select>
              <label for="status">Trạng thái</label>
              <select id="status" name="status" required>
                <option value="" disabled selected>Chọn trạng thái</option>
                <option value="Completed">Hoàn thành</option>
                <option value="Pending">Chờ xử lý</option>
                <option value="Failed">Thất bại</option>
                <option value="Refunded">Hoàn tiền</option>
              </select>
              <div style="grid-column: 2 / 3; margin-top: 1.5rem;">
                <button type="submit" class="btn" id="submitBtn">Lưu thanh toán</button>
                <button type="button" class="btn" id="cancelBtn" style="background:#ef4444; margin-left: 12px;">Hủy</button>
              </div>
            </form>
          `;

            setTimeout(setupPaymentFormJS);
        }
    });
});

// Setup JS for payment form after injecting html
function setupPaymentFormJS() {
    payments = payments || [];
    editingPaymentId = null;
    const paymentTableBodyDyn = document.getElementById('paymentTableBody');
    const paymentFormDyn = document.getElementById('paymentForm');
    const formTitleDyn = document.getElementById('formTitle');
    const submitBtnDyn = document.getElementById('submitBtn');
    const cancelBtnDyn = document.getElementById('cancelBtn');
    const btnAddPaymentDyn = document.getElementById('btnAddPayment');

    const inputPaymentCodeDyn = document.getElementById('paymentCode');
    const inputBookingCodeDyn = document.getElementById('bookingCode');
    const inputPaymentDateDyn = document.getElementById('paymentDate');
    const inputAmountDyn = document.getElementById('amount');
    const selectMethodDyn = document.getElementById('method');
    const selectStatusDyn = document.getElementById('status');

    function renderPaymentTableDyn() {
        paymentTableBodyDyn.innerHTML = '';
        payments.forEach((pay, index) => {
            const tr = document.createElement('tr');
            tr.tabIndex = 0;
            tr.innerHTML = `
            <td>${index + 1}</td>
            <td>${pay.code}</td>
            <td>${pay.bookingCode}</td>
            <td>${pay.date}</td>
            <td>${pay.amount.toLocaleString('vi-VN')} VNĐ</td>
            <td>${pay.method}</td>
            <td>${statusTextDyn(pay.status)}</td>
            <td>
              <button class="btn btnEdit" data-id="${pay.id}" style="padding: 6px 12px; font-size: 0.8rem;">Sửa</button>
              <button class="btn btnDelete" data-id="${pay.id}" style="padding: 6px 12px; font-size: 0.8rem; background:#ef4444; box-shadow:none;">Xóa</button>
            </td>`;
            paymentTableBodyDyn.appendChild(tr);
        });
        attachTableButtonEventsDyn();
    }
    function statusTextDyn(st) {
        switch(st){
            case 'Completed': return 'Hoàn thành';
            case 'Pending': return 'Chờ xử lý';
            case 'Failed': return 'Thất bại';
            case 'Refunded': return 'Hoàn tiền';
            default: return st;
        }
    }
    function clearFormDyn() {
        paymentFormDyn.reset();
        editingPaymentId = null;
        formTitleDyn.textContent = "Tạo thanh toán mới";
        submitBtnDyn.textContent = "Lưu thanh toán";
    }
    function showFormDyn() {
        paymentFormDyn.style.display = 'grid';
        btnAddPaymentDyn.style.display = 'none';
        window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});
        inputPaymentCodeDyn.focus();
    }
    function hideFormDyn() {
        paymentFormDyn.style.display = 'none';
        btnAddPaymentDyn.style.display = 'inline-block';
        clearFormDyn();
    }
    function attachTableButtonEventsDyn() {
        document.querySelectorAll('.btnEdit').forEach(btn => {
            btn.onclick = e => {
                const id = Number(e.target.dataset.id);
                editPaymentDyn(id);
            };
        });
        document.querySelectorAll('.btnDelete').forEach(btn => {
            btn.onclick = e => {
                const id = Number(e.target.dataset.id);
                deletePaymentDyn(id);
            };
        });
    }
    function editPaymentDyn(id) {
        const payment = payments.find(p => p.id === id);
        if (!payment) return;
        editingPaymentId = id;
        formTitleDyn.textContent = "Cập nhật thanh toán";
        submitBtnDyn.textContent = "Cập nhật";
        inputPaymentCodeDyn.value = payment.code;
        inputBookingCodeDyn.value = payment.bookingCode;
        inputPaymentDateDyn.value = payment.date;
        inputAmountDyn.value = payment.amount;
        selectMethodDyn.value = payment.method;
        selectStatusDyn.value = payment.status;
        showFormDyn();
    }
    function deletePaymentDyn(id) {
        if (confirm('Bạn có chắc chắn muốn xóa thanh toán này?')) {
            payments = payments.filter(p => p.id !== id);
            renderPaymentTableDyn();
        }
    }
    paymentFormDyn.onsubmit = e => {
        e.preventDefault();
        const code = inputPaymentCodeDyn.value.trim();
        const bookingCodeValue = inputBookingCodeDyn.value.trim();
        const date = inputPaymentDateDyn.value;
        const amount = Number(inputAmountDyn.value);
        const method = selectMethodDyn.value;
        const status = selectStatusDyn.value;
        if (!code || !bookingCodeValue || !date || !amount || !method || !status) {
            alert('Vui lòng điền đầy đủ thông tin.');
            return;
        }
        if (amount <= 0) {
            alert('Số tiền phải lớn hơn 0.');
            return;
        }
        if (!editingPaymentId && payments.some(p => p.code === code)) {
            alert('Mã thanh toán đã tồn tại.');
            return;
        }
        if (editingPaymentId) {
            const idx = payments.findIndex(p => p.id === editingPaymentId);
            if (idx === -1) return;
            payments[idx] = { id: editingPaymentId, code, bookingCode: bookingCodeValue, date, amount, method, status };
            alert('Cập nhật thanh toán thành công.');
        } else {
            const newId = payments.length ? Math.max(...payments.map(p => p.id)) + 1 : 1;
            payments.push({ id: newId, code, bookingCode: bookingCodeValue, date, amount, method, status });
            alert('Tạo thanh toán mới thành công.');
        }
        renderPaymentTableDyn();
        hideFormDyn();
    };
    cancelBtnDyn.onclick = () => {
        hideFormDyn();
    };
    btnAddPaymentDyn.onclick = () => {
        clearFormDyn();
        showFormDyn();
    };
    renderPaymentTableDyn();
}
// Khởi tạo trang với dữ liệu và xử lý form
setupPaymentFormJS();
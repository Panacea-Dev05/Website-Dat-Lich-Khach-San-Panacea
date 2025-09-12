// Quản lý kho - Nhân viên
// Chỉ bao gồm các chức năng được phép cho nhân viên

// Khởi tạo dữ liệu
let filteredItems = [];
let filteredTransactions = [];
let currentItemPage = 1;
let currentTransactionPage = 1;
const itemsPerPage = 10;
const transactionsPerPage = 10;

// Hiển thị thông báo
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.classList.add('show');
    }, 100);
    
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
    }, 3000);
}

// Hiển thị trạng thái loading
function showLoading(show = true) {
    const loader = document.getElementById('loader');
    if (loader) {
        loader.style.display = show ? 'flex' : 'none';
    }
}

// Cập nhật bảng vật phẩm
function updateItemTable() {
    const tbody = document.querySelector('#itemTable tbody');
    if (!tbody) return;
    
    const startIndex = (currentItemPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const pageItems = filteredItems.slice(startIndex, endIndex);
    
    tbody.innerHTML = pageItems.map(item => `
        <tr>
            <td>${item.maVatPham}</td>
            <td>${item.tenVatPham}</td>
            <td>${item.loaiVatPham}</td>
            <td>${item.soLuongTonKho}</td>
            <td>${item.donGia ? item.donGia.toLocaleString('vi-VN') + ' VNĐ' : 'N/A'}</td>
            <td>${item.nhaCungCap || 'N/A'}</td>
            <td>${item.hanSuDung ? new Date(item.hanSuDung).toLocaleDateString('vi-VN') : 'N/A'}</td>
        </tr>
    `).join('');
    
    updateItemPagination();
}

// Cập nhật phân trang vật phẩm
function updateItemPagination() {
    const totalPages = Math.ceil(filteredItems.length / itemsPerPage);
    const pagination = document.getElementById('itemPagination');
    if (!pagination) return;
    
    pagination.innerHTML = `
        <button onclick="changeItemPage(${currentItemPage - 1})" ${currentItemPage === 1 ? 'disabled' : ''}>Trước</button>
        <span>Trang ${currentItemPage} / ${totalPages}</span>
        <button onclick="changeItemPage(${currentItemPage + 1})" ${currentItemPage === totalPages ? 'disabled' : ''}>Sau</button>
    `;
}

// Thay đổi trang vật phẩm
function changeItemPage(page) {
    const totalPages = Math.ceil(filteredItems.length / itemsPerPage);
    if (page >= 1 && page <= totalPages) {
        currentItemPage = page;
        updateItemTable();
    }
}

// Cập nhật bảng giao dịch
function updateTransactionTable() {
    const tbody = document.querySelector('#transactionTable tbody');
    if (!tbody) return;
    
    const startIndex = (currentTransactionPage - 1) * transactionsPerPage;
    const endIndex = startIndex + transactionsPerPage;
    const pageTransactions = filteredTransactions.slice(startIndex, endIndex);
    
    tbody.innerHTML = pageTransactions.map(transaction => `
        <tr>
            <td>${transaction.maGiaoDich}</td>
            <td>${transaction.loaiGiaoDich}</td>
            <td>${transaction.vatPham?.tenVatPham || 'N/A'}</td>
            <td>${transaction.soLuong}</td>
            <td>${new Date(transaction.ngayGiaoDich).toLocaleDateString('vi-VN')}</td>
            <td>${transaction.nguoiThucHien}</td>
            <td>
                <span class="badge ${transaction.trangThaiPheDuyet === 'APPROVED' ? 'success' : 
                    transaction.trangThaiPheDuyet === 'PENDING' ? 'warning' : 'danger'}">
                    ${transaction.trangThaiPheDuyet === 'APPROVED' ? 'Đã phê duyệt' : 
                      transaction.trangThaiPheDuyet === 'PENDING' ? 'Chờ phê duyệt' : 'Từ chối'}
                </span>
            </td>
        </tr>
    `).join('');
    
    updateTransactionPagination();
}

// Cập nhật phân trang giao dịch
function updateTransactionPagination() {
    const totalPages = Math.ceil(filteredTransactions.length / transactionsPerPage);
    const pagination = document.getElementById('transactionPagination');
    if (!pagination) return;
    
    pagination.innerHTML = `
        <button onclick="changeTransactionPage(${currentTransactionPage - 1})" ${currentTransactionPage === 1 ? 'disabled' : ''}>Trước</button>
        <span>Trang ${currentTransactionPage} / ${totalPages}</span>
        <button onclick="changeTransactionPage(${currentTransactionPage + 1})" ${currentTransactionPage === totalPages ? 'disabled' : ''}>Sau</button>
    `;
}

// Thay đổi trang giao dịch
function changeTransactionPage(page) {
    const totalPages = Math.ceil(filteredTransactions.length / transactionsPerPage);
    if (page >= 1 && page <= totalPages) {
        currentTransactionPage = page;
        updateTransactionTable();
    }
}

// Xác thực form giao dịch
function validateTransactionForm() {
    const form = document.getElementById('transactionForm');
    if (!form) return false;
    
    const loaiGiaoDich = form.querySelector('[name="loaiGiaoDich"]')?.value;
    const vatPhamId = form.querySelector('[name="vatPham.id"]')?.value;
    const soLuong = form.querySelector('[name="soLuong"]')?.value;
    
    if (!loaiGiaoDich) {
        showNotification('Vui lòng chọn loại giao dịch', 'error');
        return false;
    }
    
    if (!vatPhamId) {
        showNotification('Vui lòng chọn vật phẩm', 'error');
        return false;
    }
    
    if (!soLuong || soLuong <= 0) {
        showNotification('Vui lòng nhập số lượng hợp lệ', 'error');
        return false;
    }
    
    return true;
}

// Gửi form giao dịch
function submitTransactionForm() {
    if (!validateTransactionForm()) {
        return;
    }
    
    const form = document.getElementById('transactionForm');
    const formData = new FormData(form);
    
    showLoading(true);
    
    fetch('/api/transactions', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (response.ok) {
            showNotification('Tạo giao dịch thành công! Chờ phê duyệt.', 'success');
            form.reset();
            setTimeout(() => {
                window.location.reload();
            }, 1500);
        } else {
            throw new Error('Có lỗi xảy ra');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Có lỗi xảy ra khi tạo giao dịch', 'error');
    })
    .finally(() => {
        showLoading(false);
    });
}

// Khởi tạo khi trang được tải
document.addEventListener('DOMContentLoaded', function() {
    // Khởi tạo dữ liệu từ server
    if (typeof items !== 'undefined') {
        filteredItems = [...items];
        updateItemTable();
    }
    
    if (typeof transactions !== 'undefined') {
        filteredTransactions = [...transactions];
        updateTransactionTable();
    }
    
    // Xử lý form giao dịch
    const transactionForm = document.getElementById('transactionForm');
    if (transactionForm) {
        transactionForm.addEventListener('submit', function(e) {
            e.preventDefault();
            submitTransactionForm();
        });
    }
    
    // Xử lý nút thêm giao dịch
    const addTransactionBtn = document.getElementById('addTransactionBtn');
    if (addTransactionBtn) {
        addTransactionBtn.addEventListener('click', function(e) {
            e.preventDefault();
            submitTransactionForm();
        });
    }
    
    // Xử lý tìm kiếm vật phẩm
    const itemSearch = document.getElementById('itemSearch');
    if (itemSearch) {
        itemSearch.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            filteredItems = items.filter(item => 
                item.tenVatPham.toLowerCase().includes(searchTerm) ||
                item.maVatPham.toLowerCase().includes(searchTerm)
            );
            currentItemPage = 1;
            updateItemTable();
        });
    }
    
    // Xử lý tìm kiếm giao dịch
    const transactionSearch = document.getElementById('transactionSearch');
    if (transactionSearch) {
        transactionSearch.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            filteredTransactions = transactions.filter(transaction => 
                transaction.maGiaoDich.toLowerCase().includes(searchTerm) ||
                (transaction.vatPham?.tenVatPham || '').toLowerCase().includes(searchTerm)
            );
            currentTransactionPage = 1;
            updateTransactionTable();
        });
    }
});
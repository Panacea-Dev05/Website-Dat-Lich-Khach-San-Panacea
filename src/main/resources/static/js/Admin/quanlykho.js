// Global variables
let currentEditingItemId = null;

// Form handling
document.getElementById('itemForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    if (!validateItemForm()) {
        return;
    }
    
    const formData = new FormData(this);
    const itemData = {
        tenVatPham: formData.get('tenVatPham'),
        maVatPham: formData.get('maVatPham'),
        loaiVatPham: formData.get('loaiVatPham'),
        soLuongTon: parseInt(formData.get('soLuongTon')) || 0,
        soLuongToiThieu: parseInt(formData.get('soLuongToiThieu')) || 0,
        giaNhap: parseFloat(formData.get('giaNhap')) || 0,
        donViTinh: formData.get('donViTinh'),
        viTriKho: formData.get('viTriKho'),
        nhaCungCap: formData.get('nhaCungCap'),
        hanSuDung: formData.get('hanSuDung') || null,
        trangThai: formData.get('trangThai')
    };
    
    if (currentEditingItemId) {
        updateItem(currentEditingItemId, itemData);
    } else {
        createItem(itemData);
    }
});

// Validation function
function validateItemForm() {
    const tenVatPham = document.querySelector('input[name="tenVatPham"]').value.trim();
    const maVatPham = document.querySelector('input[name="maVatPham"]').value.trim();
    
    if (!tenVatPham) {
        showNotification('Vui lòng nhập tên vật phẩm', 'error');
        return false;
    }
    
    if (!maVatPham) {
        showNotification('Vui lòng nhập mã vật phẩm', 'error');
        return false;
    }
    
    return true;
}

// Create item function
function createItem(itemData) {
    fetch('/admin/inventory/items', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: JSON.stringify(itemData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showNotification('Tạo vật phẩm thành công!', 'success');
            resetForm();
            location.reload(); // Reload to show new item
        } else {
            showNotification(data.message || 'Có lỗi xảy ra khi tạo vật phẩm', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Có lỗi xảy ra khi tạo vật phẩm', 'error');
    });
}

// Update item function
function updateItem(id, itemData) {
    fetch(`/admin/inventory/items/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: JSON.stringify(itemData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showNotification('Cập nhật vật phẩm thành công!', 'success');
            resetForm();
            currentEditingItemId = null;
            location.reload(); // Reload to show updated item
        } else {
            showNotification(data.message || 'Có lỗi xảy ra khi cập nhật vật phẩm', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Có lỗi xảy ra khi cập nhật vật phẩm', 'error');
    });
}

document.getElementById('transactionForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    if (!validateTransactionForm()) {
        return;
    }
    
    const formData = new FormData(this);
    const transactionData = {
        vatPhamId: parseInt(formData.get('vatPham')),
        loaiGiaoDich: formData.get('loaiGiaoDich'),
        soLuong: parseInt(formData.get('soLuong')),
        giaTriGiaoDich: parseFloat(formData.get('giaTriGiaoDich')) || 0,
        ngayGiaoDich: formData.get('ngayGiaoDich'),
        lyDo: formData.get('lyDo') || ''
    };
    
    createTransaction(transactionData);
});

// Validation function for transaction form
function validateTransactionForm() {
    const vatPham = document.querySelector('select[name="vatPham"]').value;
    const loaiGiaoDich = document.querySelector('select[name="loaiGiaoDich"]').value;
    const soLuong = document.querySelector('input[name="soLuong"]').value;
    
    if (!vatPham) {
        showNotification('Vui lòng chọn vật phẩm', 'error');
        return false;
    }
    
    if (!loaiGiaoDich) {
        showNotification('Vui lòng chọn loại giao dịch', 'error');
        return false;
    }
    
    if (!soLuong || parseInt(soLuong) <= 0) {
        showNotification('Vui lòng nhập số lượng hợp lệ', 'error');
        return false;
    }
    
    return true;
}

// Create transaction function
function createTransaction(transactionData) {
    fetch('/admin/inventory/transactions', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: JSON.stringify(transactionData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showNotification('Tạo giao dịch thành công!', 'success');
            document.getElementById('transactionForm').reset();
            
            // Reset date to today
            const today = new Date().toISOString().split('T')[0];
            const transactionDateInput = document.querySelector('input[name="ngayGiaoDich"]');
            if (transactionDateInput) {
                transactionDateInput.value = today;
            }
            
            location.reload(); // Reload to show updated inventory and transaction history
        } else {
            showNotification(data.message || 'Có lỗi xảy ra khi tạo giao dịch', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Có lỗi xảy ra khi tạo giao dịch', 'error');
    });
}

// Search and filter functions
function searchItems() {
    const searchTerm = document.querySelector('input[name="search"]').value.trim();
    const typeFilter = document.querySelector('select[name="typeFilter"]').value;
    const statusFilter = document.querySelector('select[name="statusFilter"]').value;
    
    // Build query parameters
    const params = new URLSearchParams();
    if (searchTerm) params.append('search', searchTerm);
    if (typeFilter) params.append('type', typeFilter);
    if (statusFilter) params.append('status', statusFilter);
    
    // Redirect to filtered page
    window.location.href = `/admin/inventory?${params.toString()}`;
}

function filterByType() {
    searchItems(); // Use the unified search function
}

function filterByStatus() {
    searchItems(); // Use the unified search function
}

function resetSearch() {
    const searchInput = document.querySelector('input[name="search"]');
    const typeFilter = document.querySelector('select[name="typeFilter"]');
    const statusFilter = document.querySelector('select[name="statusFilter"]');
    
    if (searchInput) searchInput.value = '';
    if (typeFilter) typeFilter.value = '';
    if (statusFilter) statusFilter.value = '';
    
    searchItems();
}

function resetForm() {
    document.getElementById('itemForm').reset();
    currentEditingItemId = null;
    
    // Reset form title
    const formTitle = document.querySelector('#itemForm h3');
    if (formTitle) {
        formTitle.textContent = 'Tạo vật phẩm mới';
    }
    
    // Reset submit button text
    const submitBtn = document.querySelector('#itemForm button[type="submit"]');
    if (submitBtn) {
        submitBtn.textContent = 'Tạo vật phẩm';
    }
}

function changePageSize() {
    const pageSize = document.getElementById('pageSize').value;
    console.log('Changing page size to:', pageSize);
    // Implement pagination
}

// Item actions
function editItem(id) {
    fetch(`/admin/inventory/items/${id}`, {
        method: 'GET',
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success && data.item) {
            populateFormForEdit(data.item);
            currentEditingItemId = id;
            
            // Scroll to form
            document.getElementById('itemForm').scrollIntoView({ behavior: 'smooth' });
        } else {
            showNotification('Không thể tải thông tin vật phẩm', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Có lỗi xảy ra khi tải thông tin vật phẩm', 'error');
    });
}

function deleteItem(id) {
    if (!confirm('Bạn có chắc chắn muốn xóa vật phẩm này?')) {
        return;
    }
    
    fetch(`/admin/inventory/items/${id}`, {
        method: 'DELETE',
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showNotification('Xóa vật phẩm thành công!', 'success');
            location.reload(); // Reload to remove deleted item
        } else {
            showNotification(data.message || 'Có lỗi xảy ra khi xóa vật phẩm', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Có lỗi xảy ra khi xóa vật phẩm', 'error');
    });
}

// Populate form for editing
function populateFormForEdit(item) {
    document.querySelector('input[name="tenVatPham"]').value = item.tenVatPham || '';
    document.querySelector('input[name="maVatPham"]').value = item.maVatPham || '';
    document.querySelector('select[name="loaiVatPham"]').value = item.loaiVatPham || '';
    document.querySelector('input[name="soLuongTon"]').value = item.soLuongTon || 0;
    document.querySelector('input[name="soLuongToiThieu"]').value = item.soLuongToiThieu || 0;
    document.querySelector('input[name="giaNhap"]').value = item.giaNhap || 0;
    document.querySelector('input[name="donViTinh"]').value = item.donViTinh || '';
    document.querySelector('input[name="viTriKho"]').value = item.viTriKho || '';
    document.querySelector('input[name="nhaCungCap"]').value = item.nhaCungCap || '';
    document.querySelector('input[name="hanSuDung"]').value = item.hanSuDung || '';
    document.querySelector('select[name="trangThai"]').value = item.trangThai || 'Hoạt động';
    
    // Change form title
    const formTitle = document.querySelector('#itemForm h3');
    if (formTitle) {
        formTitle.textContent = 'Cập nhật vật phẩm';
    }
    
    // Change submit button text
    const submitBtn = document.querySelector('#itemForm button[type="submit"]');
    if (submitBtn) {
        submitBtn.textContent = 'Cập nhật';
    }
}

// Report functions
function exportToExcel() {
    // Get current filters
    const searchTerm = document.querySelector('input[name="search"]')?.value || '';
    const typeFilter = document.querySelector('select[name="typeFilter"]')?.value || '';
    const statusFilter = document.querySelector('select[name="statusFilter"]')?.value || '';
    
    // Build query parameters
    const params = new URLSearchParams();
    if (searchTerm) params.append('search', searchTerm);
    if (typeFilter) params.append('type', typeFilter);
    if (statusFilter) params.append('status', statusFilter);
    
    // Create download link
    const exportUrl = `/admin/inventory/export/excel?${params.toString()}`;
    
    // Show loading notification
    showNotification('Đang xuất file Excel...', 'info');
    
    // Create temporary link and trigger download
    const link = document.createElement('a');
    link.href = exportUrl;
    link.download = `inventory_${new Date().toISOString().split('T')[0]}.xlsx`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    // Show success notification after a delay
    setTimeout(() => {
        showNotification('Xuất file Excel thành công!', 'success');
    }, 1000);
}

function generateReport() {
    // Get date range if available
    const startDate = document.querySelector('input[name="startDate"]')?.value;
    const endDate = document.querySelector('input[name="endDate"]')?.value;
    
    // Build query parameters
    const params = new URLSearchParams();
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    
    // Show loading notification
    showNotification('Đang tạo báo cáo...', 'info');
    
    fetch(`/admin/inventory/reports/generate?${params.toString()}`, {
        method: 'GET',
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => {
        if (response.ok) {
            return response.blob();
        }
        throw new Error('Không thể tạo báo cáo');
    })
    .then(blob => {
        // Create download link for PDF report
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `inventory_report_${new Date().toISOString().split('T')[0]}.pdf`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
        
        showNotification('Tạo báo cáo thành công!', 'success');
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Có lỗi xảy ra khi tạo báo cáo', 'error');
    });
}

// Notification function
function showNotification(message, type = 'info') {
    // Remove existing notifications
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(notification => notification.remove());
    
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <span>${message}</span>
        <button onclick="this.parentElement.remove()">&times;</button>
    `;
    
    // Add styles
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 5px;
        color: white;
        font-weight: bold;
        z-index: 10000;
        max-width: 300px;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        display: flex;
        justify-content: space-between;
        align-items: center;
    `;
    
    // Set background color based on type
    switch(type) {
        case 'success':
            notification.style.backgroundColor = '#28a745';
            break;
        case 'error':
            notification.style.backgroundColor = '#dc3545';
            break;
        case 'warning':
            notification.style.backgroundColor = '#ffc107';
            notification.style.color = '#212529';
            break;
        case 'info':
        default:
            notification.style.backgroundColor = '#17a2b8';
            break;
    }
    
    // Style the close button
    const closeBtn = notification.querySelector('button');
    closeBtn.style.cssText = `
        background: none;
        border: none;
        color: inherit;
        font-size: 18px;
        cursor: pointer;
        margin-left: 10px;
        padding: 0;
        line-height: 1;
    `;
    
    // Add to page
    document.body.appendChild(notification);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        if (notification.parentElement) {
            notification.remove();
        }
    }, 5000);
}

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    // Set current date for transaction form
    const today = new Date().toISOString().split('T')[0];
    const transactionDateInput = document.querySelector('input[name="ngayGiaoDich"]');
    if (transactionDateInput) {
        transactionDateInput.value = today;
    }
    
    // Search input event listener
    const searchInput = document.querySelector('input[name="search"]');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                searchItems();
            }
        });
    }
    
    // Filter select event listeners
    const typeFilter = document.querySelector('select[name="typeFilter"]');
    if (typeFilter) {
        typeFilter.addEventListener('change', filterByType);
    }
    
    const statusFilter = document.querySelector('select[name="statusFilter"]');
    if (statusFilter) {
        statusFilter.addEventListener('change', filterByStatus);
    }
    
    // Search button event listener
    const searchBtn = document.querySelector('.search-bar button');
    if (searchBtn) {
        searchBtn.addEventListener('click', function(e) {
            e.preventDefault();
            searchItems();
        });
    }
    
    // Reset search button event listener
    const resetBtn = document.querySelector('.reset-search-btn');
    if (resetBtn) {
        resetBtn.addEventListener('click', function(e) {
            e.preventDefault();
            resetSearch();
        });
    }
});
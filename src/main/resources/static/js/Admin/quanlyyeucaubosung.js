document.addEventListener('DOMContentLoaded', function() {
    const mobileToggle = document.getElementById('mobileToggle');
    const sidebar = document.getElementById('sidebar');

    if (mobileToggle && sidebar) {
        mobileToggle.addEventListener('click', () => {
            sidebar.classList.toggle('open');
        });
    }

    // Initialize page
    loadSupplyRequests();
});

// Load supply requests
function loadSupplyRequests() {
    // This function would load supply requests from the server
    console.log('Loading supply requests...');
}

function viewRequest(id) {
    fetch(`/admin/supply-requests/${id}`)
        .then(response => response.json())
        .then(request => {
            const details = `
                <div class="form-group">
                    <label>Vật phẩm:</label>
                    <p>${request.vatPham?.tenVatPham || 'N/A'}</p>
                </div>
                <div class="form-group">
                    <label>Số lượng yêu cầu:</label>
                    <p>${request.soLuongYeuCau}</p>
                </div>
                <div class="form-group">
                    <label>Nhân viên yêu cầu:</label>
                    <p>${request.nhanVien?.ho + ' ' + request.nhanVien?.ten || 'N/A'}</p>
                </div>
                <div class="form-group">
                    <label>Mức độ ưu tiên:</label>
                    <p>${request.mucDoUuTien}</p>
                </div>
                <div class="form-group">
                    <label>Lý do yêu cầu:</label>
                    <p>${request.lyDoYeuCau || 'Không có'}</p>
                </div>
                <div class="form-group">
                    <label>Ngày yêu cầu:</label>
                    <p>${new Date(request.ngayYeuCau).toLocaleString('vi-VN')}</p>
                </div>
                <div class="form-group">
                    <label>Trạng thái:</label>
                    <p>${getStatusText(request.trangThai)}</p>
                </div>
                ${request.ghiChu ? `
                    <div class="form-group">
                        <label>Ghi chú admin:</label>
                        <p>${request.ghiChu}</p>
                    </div>
                ` : ''}
                ${request.lyDoTuChoi ? `
                    <div class="form-group">
                        <label>Lý do từ chối:</label>
                        <p>${request.lyDoTuChoi}</p>
                    </div>
                ` : ''}
            `;
            document.getElementById('requestDetails').innerHTML = details;
            document.getElementById('viewModal').style.display = 'block';
        })
        .catch(error => {
            console.error('Error loading request details:', error);
            showNotification('Không thể tải thông tin yêu cầu', 'error');
        });
}

function closeViewModal() {
    document.getElementById('viewModal').style.display = 'none';
}

function approveRequest(id) {
    if (confirm('Bạn có chắc chắn muốn duyệt yêu cầu này?')) {
        fetch(`/admin/supply-requests/${id}/approve`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showNotification('Duyệt yêu cầu thành công', 'success');
                location.reload();
            } else {
                showNotification(data.message || 'Có lỗi xảy ra', 'error');
            }
        })
        .catch(error => {
            console.error('Error approving request:', error);
            showNotification('Có lỗi xảy ra khi duyệt yêu cầu', 'error');
        });
    }
}

function rejectRequest(id) {
    const reason = prompt('Nhập lý do từ chối:');
    if (reason) {
        fetch(`/admin/supply-requests/${id}/reject`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ lyDoTuChoi: reason })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showNotification('Từ chối yêu cầu thành công', 'success');
                location.reload();
            } else {
                showNotification(data.message || 'Có lỗi xảy ra', 'error');
            }
        })
        .catch(error => {
            console.error('Error rejecting request:', error);
            showNotification('Có lỗi xảy ra khi từ chối yêu cầu', 'error');
        });
    }
}

function getStatusText(status) {
    switch(status) {
        case 'CHO_DUYET': return 'Chờ duyệt';
        case 'DA_DUYET': return 'Đã duyệt';
        case 'TU_CHOI': return 'Từ chối';
        case 'HOAN_THANH': return 'Hoàn thành';
        default: return status;
    }
}

function searchRequests() {
    const searchValue = document.querySelector('input[name="search"]').value.trim();
    const statusFilter = document.querySelector('select[name="statusFilter"]').value;
    const priorityFilter = document.querySelector('select[name="priorityFilter"]').value;
    
    const params = new URLSearchParams();
    if (searchValue) params.append('search', searchValue);
    if (statusFilter) params.append('status', statusFilter);
    if (priorityFilter) params.append('priority', priorityFilter);
    
    window.location.href = `/admin/supply-requests?${params.toString()}`;
}

function filterByStatus() {
    searchRequests();
}

function filterByPriority() {
    searchRequests();
}

function resetSearch() {
    document.querySelector('input[name="search"]').value = '';
    document.querySelector('select[name="statusFilter"]').value = '';
    document.querySelector('select[name="priorityFilter"]').value = '';
    window.location.href = '/admin/supply-requests';
}

function changePageSize() {
    const pageSize = document.querySelector('select[name="pageSize"]').value;
    const url = new URL(window.location);
    url.searchParams.set('size', pageSize);
    window.location.href = url.toString();
}

function showNotification(message, type = 'info') {
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(notification => notification.remove());
    
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <span>${message}</span>
        <button class="notification-close" onclick="this.parentElement.remove()">&times;</button>
    `;
    
    const styles = {
        position: 'fixed',
        top: '20px',
        right: '20px',
        padding: '15px 20px',
        borderRadius: '5px',
        color: 'white',
        fontWeight: 'bold',
        zIndex: '10000',
        minWidth: '300px',
        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
    };
    
    Object.assign(notification.style, styles);
    
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
    
    const closeBtn = notification.querySelector('.notification-close');
    if (closeBtn) {
        closeBtn.style.background = 'none';
        closeBtn.style.border = 'none';
        closeBtn.style.color = 'inherit';
        closeBtn.style.fontSize = '18px';
        closeBtn.style.cursor = 'pointer';
        closeBtn.style.marginLeft = '10px';
    }
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        if (notification.parentElement) {
            notification.remove();
        }
    }, 5000);
}
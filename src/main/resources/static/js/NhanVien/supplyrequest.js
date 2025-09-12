let currentRequests = [];
let currentFilter = 'all';
let currentAction = null;
let currentRequestId = null;
const isAdmin = /*[[${isAdmin}]]*/ false;

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    loadInventoryItems();
    loadRequests();
    setupEventListeners();
});

function setupEventListeners() {
    // Filter tabs
    document.querySelectorAll('.tab').forEach(tab => {
        tab.addEventListener('click', function() {
            document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
            this.classList.add('active');
            currentFilter = this.dataset.status;
            filterRequests();
        });
    });

    // Create form
    document.getElementById('createForm').addEventListener('submit', handleCreateRequest);
    
    // Action form
    document.getElementById('actionForm').addEventListener('submit', handleActionRequest);
}

async function loadInventoryItems() {
    try {
        const response = await fetch('/nhanvien/quanlykho/api/items');
        const data = await response.json();
        
        const select = document.getElementById('vatPhamId');
        select.innerHTML = '<option value="">Chọn vật phẩm</option>';
        
        if (data.success && data.items) {
            data.items.forEach(item => {
                const option = document.createElement('option');
                option.value = item.id;
                option.textContent = `${item.tenVatPham} (Tồn: ${item.soLuongTon})`;
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error loading inventory items:', error);
    }
}

async function loadRequests() {
    try {
        // Hiển thị loading state
        showLoadingState(true);
        
        const response = await fetch('/nhanvien/supply-request/api/my-requests');
        const data = await response.json();
        
        if (data.success) {
            currentRequests = data.requests || [];
            updateStatistics();
            filterRequests();
        } else {
            showNotification('Lỗi khi tải dữ liệu: ' + (data.message || 'Không xác định'), 'error');
        }
    } catch (error) {
        console.error('Error loading requests:', error);
        showNotification('Không thể kết nối đến server. Vui lòng thử lại!', 'error');
    } finally {
        showLoadingState(false);
    }
}

function updateStatistics() {
    const totalCount = currentRequests.length;
    const pendingCount = currentRequests.filter(r => r.trangThai === 'CHO_DUYET').length;
    const approvedCount = currentRequests.filter(r => r.trangThai === 'DA_DUYET').length;
    const rejectedCount = currentRequests.filter(r => r.trangThai === 'TU_CHOI').length;
    const completedCount = currentRequests.filter(r => r.trangThai === 'DA_THUC_HIEN').length;
    
    // Cập nhật với hiệu ứng animation
    animateCounter('totalCount', totalCount);
    animateCounter('pendingCount', pendingCount);
    animateCounter('approvedCount', approvedCount);
    animateCounter('rejectedCount', rejectedCount);
    animateCounter('completedCount', completedCount);
}

function animateCounter(elementId, targetValue) {
    const element = document.getElementById(elementId);
    const currentValue = parseInt(element.textContent) || 0;
    const increment = targetValue > currentValue ? 1 : -1;
    const stepTime = Math.abs(Math.floor(200 / (targetValue - currentValue))) || 1;
    
    if (currentValue !== targetValue) {
        const timer = setInterval(() => {
            const current = parseInt(element.textContent);
            if (current !== targetValue) {
                element.textContent = current + increment;
            } else {
                clearInterval(timer);
            }
        }, stepTime);
    }
}

function filterRequests() {
    let filteredRequests = currentRequests;
    
    if (currentFilter !== 'all') {
        filteredRequests = currentRequests.filter(request => request.trangThai === currentFilter);
    }
    
    renderRequestsTable(filteredRequests);
}

function renderRequestsTable(requests) {
    const tbody = document.getElementById('requestsTableBody');
    const emptyState = document.getElementById('emptyState');
    
    if (requests.length === 0) {
        tbody.innerHTML = '';
        emptyState.style.display = 'block';
        return;
    }
    
    emptyState.style.display = 'none';
    
    tbody.innerHTML = requests.map(request => {
        const statusClass = getStatusClass(request.trangThai);
        const priorityClass = getPriorityClass(request.mucDoUuTien);
        const date = new Date(request.ngayYeuCau).toLocaleDateString('vi-VN');
        
        let actions = '';
        if (isAdmin && request.trangThai === 'CHO_DUYET') {
            actions = `
                <button class="btn btn-success btn-sm" onclick="openActionModal(${request.id}, 'approve')">
                    <span class="material-icons">check</span>
                </button>
                <button class="btn btn-danger btn-sm" onclick="openActionModal(${request.id}, 'reject')">
                    <span class="material-icons">close</span>
                </button>
            `;
        } else if (isAdmin && request.trangThai === 'DA_DUYET') {
            actions = `
                <button class="btn btn-primary btn-sm" onclick="markAsCompleted(${request.id})">
                    <span class="material-icons">done_all</span>
                </button>
            `;
        }
        
        return `
            <tr>
                <td>#${request.id}</td>
                <td>${request.tenVatPham || 'N/A'}</td>
                <td>${request.soLuongYeuCau}</td>
                <td>${request.tenNhanVienYeuCau || 'N/A'}</td>
                <td>${date}</td>
                <td><span class="priority-badge ${priorityClass}">${request.mucDoUuTien}</span></td>
                <td><span class="status-badge ${statusClass}">${getStatusText(request.trangThai)}</span></td>
                ${isAdmin ? `<td>${actions}</td>` : ''}
            </tr>
        `;
    }).join('');
}

function getStatusClass(status) {
    switch (status) {
        case 'CHO_DUYET': return 'status-pending';
        case 'DA_DUYET': return 'status-approved';
        case 'TU_CHOI': return 'status-rejected';
        case 'DA_THUC_HIEN': return 'status-completed';
        default: return '';
    }
}

function getStatusText(status) {
    switch (status) {
        case 'CHO_DUYET': return 'Chờ duyệt';
        case 'DA_DUYET': return 'Đã duyệt';
        case 'TU_CHOI': return 'Từ chối';
        case 'DA_THUC_HIEN': return 'Hoàn thành';
        default: return status;
    }
}

function getPriorityClass(priority) {
    switch (priority) {
        case 'Khẩn cấp':
        case 'Cao':
            return 'priority-high';
        case 'Thấp':
            return 'priority-low';
        default:
            return 'priority-normal';
    }
}

// Modal functions
function openCreateModal() {
    document.getElementById('createModal').style.display = 'block';
}

function closeCreateModal() {
    document.getElementById('createModal').style.display = 'none';
    document.getElementById('createForm').reset();
}

function openActionModal(requestId, action) {
    currentRequestId = requestId;
    currentAction = action;
    
    const modal = document.getElementById('actionModal');
    const title = document.getElementById('actionModalTitle');
    const label = document.getElementById('actionLabel');
    const submitBtn = document.getElementById('actionSubmitBtn');
    
    if (action === 'approve') {
        title.textContent = 'Phê duyệt yêu cầu';
        label.textContent = 'Ghi chú phê duyệt';
        submitBtn.textContent = 'Phê duyệt';
        submitBtn.className = 'btn btn-success';
    } else {
        title.textContent = 'Từ chối yêu cầu';
        label.textContent = 'Lý do từ chối *';
        submitBtn.textContent = 'Từ chối';
        submitBtn.className = 'btn btn-danger';
    }
    
    modal.style.display = 'block';
}

function closeActionModal() {
    document.getElementById('actionModal').style.display = 'none';
    document.getElementById('actionForm').reset();
    currentRequestId = null;
    currentAction = null;
}

// Form handlers
async function handleCreateRequest(e) {
    e.preventDefault();
    
    const formData = {
        vatPhamId: parseInt(document.getElementById('vatPhamId').value),
        soLuongYeuCau: parseInt(document.getElementById('soLuongYeuCau').value),
        mucDoUuTien: document.getElementById('mucDoUuTien').value,
        lyDoYeuCau: document.getElementById('lyDoYeuCau').value
    };
    
    // Validation
    if (!formData.vatPhamId || !formData.soLuongYeuCau || !formData.lyDoYeuCau.trim()) {
        showNotification('Vui lòng điền đầy đủ thông tin bắt buộc!', 'warning');
        return;
    }
    
    try {
        showLoadingState(true);
        
        const response = await fetch('/nhanvien/supply-request/api/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        
        const data = await response.json();
        
        if (data.success) {
            showNotification('Yêu cầu đã được tạo thành công!', 'success');
            closeCreateModal();
            loadRequests();
        } else {
            showNotification('Lỗi: ' + data.message, 'error');
        }
    } catch (error) {
        console.error('Error creating request:', error);
        showNotification('Không thể kết nối đến server. Vui lòng thử lại!', 'error');
    } finally {
        showLoadingState(false);
    }
}

async function handleActionRequest(e) {
    e.preventDefault();
    
    const note = document.getElementById('actionNote').value;
    const endpoint = currentAction === 'approve' ? 'approve' : 'reject';
    const noteField = currentAction === 'approve' ? 'ghiChu' : 'lyDoTuChoi';
    
    try {
        const response = await fetch(`/nhanvien/supply-request/api/${endpoint}/${currentRequestId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ [noteField]: note })
        });
        
        const data = await response.json();
        
        if (data.success) {
            alert(data.message);
            closeActionModal();
            loadRequests();
        } else {
            alert('Lỗi: ' + data.message);
        }
    } catch (error) {
        console.error('Error processing request:', error);
        alert('Có lỗi xảy ra!');
    }
}

async function markAsCompleted(requestId) {
    if (!confirm('Xác nhận đánh dấu yêu cầu đã hoàn thành?')) {
        return;
    }
    
    try {
        const response = await fetch(`/nhanvien/supply-request/api/complete/${requestId}`, {
            method: 'POST'
        });
        
        const data = await response.json();
        
        if (data.success) {
            alert(data.message);
            loadRequests();
        } else {
            alert('Lỗi: ' + data.message);
        }
    } catch (error) {
        console.error('Error completing request:', error);
        alert('Có lỗi xảy ra!');
    }
}

// Close modals when clicking outside
window.onclick = function(event) {
    const createModal = document.getElementById('createModal');
    const actionModal = document.getElementById('actionModal');
    
    if (event.target === createModal) {
        closeCreateModal();
    }
    if (event.target === actionModal) {
        closeActionModal();
    }
}

// Helper functions for better UX
function showLoadingState(isLoading) {
    const loadingElements = document.querySelectorAll('.btn, .tab');
    loadingElements.forEach(element => {
        if (isLoading) {
            element.style.opacity = '0.6';
            element.style.pointerEvents = 'none';
        } else {
            element.style.opacity = '1';
            element.style.pointerEvents = 'auto';
        }
    });
    
    // Show/hide loading indicator on table
    const tableContainer = document.querySelector('.table-container');
    if (tableContainer) {
        if (isLoading) {
            tableContainer.style.opacity = '0.6';
        } else {
            tableContainer.style.opacity = '1';
        }
    }
}

function showNotification(message, type = 'info') {
    // Remove existing notifications
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(notification => notification.remove());
    
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <span class="material-icons">${getNotificationIcon(type)}</span>
            <span class="notification-message">${message}</span>
            <button class="notification-close" onclick="this.parentElement.parentElement.remove()">
                <span class="material-icons">close</span>
            </button>
        </div>
    `;
    
    // Add styles
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 10000;
        min-width: 300px;
        max-width: 500px;
        padding: 1rem;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        animation: slideInRight 0.3s ease-out;
        background: ${getNotificationColor(type)};
        border-left: 4px solid ${getNotificationBorderColor(type)};
    `;
    
    // Add to page
    document.body.appendChild(notification);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        if (notification.parentElement) {
            notification.style.animation = 'slideOutRight 0.3s ease-in';
            setTimeout(() => notification.remove(), 300);
        }
    }, 5000);
}

function getNotificationIcon(type) {
    switch (type) {
        case 'success': return 'check_circle';
        case 'error': return 'error';
        case 'warning': return 'warning';
        default: return 'info';
    }
}

function getNotificationColor(type) {
    switch (type) {
        case 'success': return 'linear-gradient(135deg, #d1fae5, #a7f3d0)';
        case 'error': return 'linear-gradient(135deg, #fee2e2, #fecaca)';
        case 'warning': return 'linear-gradient(135deg, #fef3c7, #fde68a)';
        default: return 'linear-gradient(135deg, #dbeafe, #bfdbfe)';
    }
}

function getNotificationBorderColor(type) {
    switch (type) {
        case 'success': return '#10b981';
        case 'error': return '#ef4444';
        case 'warning': return '#f59e0b';
        default: return '#3b82f6';
    }
}

// Add CSS animations
const style = document.createElement('style');
style.textContent = `
    @keyframes slideInRight {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOutRight {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
    
    .notification-content {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        color: #374151;
        font-weight: 500;
    }
    
    .notification-close {
        background: none;
        border: none;
        cursor: pointer;
        padding: 0.25rem;
        border-radius: 4px;
        margin-left: auto;
        opacity: 0.7;
        transition: opacity 0.2s;
    }
    
    .notification-close:hover {
        opacity: 1;
        background: rgba(0, 0, 0, 0.1);
    }
    
    .notification-message {
        flex: 1;
    }
`;
document.head.appendChild(style);
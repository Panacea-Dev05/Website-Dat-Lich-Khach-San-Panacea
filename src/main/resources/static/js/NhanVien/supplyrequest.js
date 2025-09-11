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
        const response = await fetch('/nhanvien/supply-request/api/my-requests');
        const data = await response.json();
        
        if (data.success) {
            currentRequests = data.requests || [];
            updateStatistics();
            filterRequests();
        }
    } catch (error) {
        console.error('Error loading requests:', error);
    }
}

function updateStatistics() {
    if (!isAdmin) return;
    
    const stats = {
        pending: 0,
        approved: 0,
        rejected: 0,
        completed: 0
    };
    
    currentRequests.forEach(request => {
        switch (request.trangThai) {
            case 'CHO_DUYET':
                stats.pending++;
                break;
            case 'DA_DUYET':
                stats.approved++;
                break;
            case 'TU_CHOI':
                stats.rejected++;
                break;
            case 'DA_THUC_HIEN':
                stats.completed++;
                break;
        }
    });
    
    document.getElementById('pendingCount').textContent = stats.pending;
    document.getElementById('approvedCount').textContent = stats.approved;
    document.getElementById('rejectedCount').textContent = stats.rejected;
    document.getElementById('completedCount').textContent = stats.completed;
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
                <td>${request.inventoryItem?.tenVatPham || 'N/A'}</td>
                <td>${request.soLuongYeuCau}</td>
                <td>${request.staffRequester?.hoTen || 'N/A'}</td>
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
    
    try {
        const response = await fetch('/nhanvien/supply-request/api/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        
        const data = await response.json();
        
        if (data.success) {
            alert('Yêu cầu đã được tạo thành công!');
            closeCreateModal();
            loadRequests();
        } else {
            alert('Lỗi: ' + data.message);
        }
    } catch (error) {
        console.error('Error creating request:', error);
        alert('Có lỗi xảy ra khi tạo yêu cầu!');
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
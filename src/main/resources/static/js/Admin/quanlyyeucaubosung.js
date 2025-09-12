let currentRequestId = null;

function filterRequests(status) {
    const rows = document.querySelectorAll('#requestsTableBody tr');
    const tabs = document.querySelectorAll('.tab');
    
    // Update active tab
    tabs.forEach(tab => tab.classList.remove('active'));
    event.target.classList.add('active');
    
    // Filter rows
    rows.forEach(row => {
        if (status === 'all' || row.getAttribute('data-status') === status) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
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
            console.error('Error:', error);
            alert('Có lỗi xảy ra khi tải thông tin yêu cầu');
        });
}

function approveRequest(id) {
    currentRequestId = id;
    document.getElementById('approveModal').style.display = 'block';
}

function rejectRequest(id) {
    currentRequestId = id;
    document.getElementById('rejectModal').style.display = 'block';
}

function markAsCompleted(id) {
    if (confirm('Bạn có chắc chắn muốn đánh dấu yêu cầu này là hoàn thành?')) {
        fetch(`/admin/supply-requests/${id}/complete`, {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('Đã đánh dấu yêu cầu hoàn thành');
                location.reload();
            } else {
                alert(data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Có lỗi xảy ra');
        });
    }
}

function confirmApprove() {
    const note = document.getElementById('approveNote').value;
    
    fetch(`/admin/supply-requests/${currentRequestId}/approve`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `ghiChu=${encodeURIComponent(note)}`
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('Đã phê duyệt yêu cầu');
            location.reload();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Có lỗi xảy ra');
    })
    .finally(() => {
        closeModal('approveModal');
    });
}

function confirmReject() {
    const reason = document.getElementById('rejectReason').value.trim();
    
    if (!reason) {
        alert('Vui lòng nhập lý do từ chối');
        return;
    }
    
    fetch(`/admin/supply-requests/${currentRequestId}/reject`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `lyDoTuChoi=${encodeURIComponent(reason)}`
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('Đã từ chối yêu cầu');
            location.reload();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Có lỗi xảy ra');
    })
    .finally(() => {
        closeModal('rejectModal');
    });
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
    if (modalId === 'approveModal') {
        document.getElementById('approveNote').value = '';
    } else if (modalId === 'rejectModal') {
        document.getElementById('rejectReason').value = '';
    }
    currentRequestId = null;
}

function getStatusText(status) {
    switch (status) {
        case 'CHO_DUYET': return 'Chờ duyệt';
        case 'DA_DUYET': return 'Đã duyệt';
        case 'TU_CHOI': return 'Từ chối';
        case 'HOAN_THANH': return 'Hoàn thành';
        default: return status;
    }
}

function getPriorityClass(priority) {
    switch (priority) {
        case 'Khẩn cấp':
            return 'priority-khẩn-cấp';
        case 'Cao':
            return 'priority-cao';
        case 'Bình thường':
            return 'priority-bình-thường';
        case 'Thấp':
            return 'priority-thấp';
        default:
            return 'priority-bình-thường';
    }
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
}
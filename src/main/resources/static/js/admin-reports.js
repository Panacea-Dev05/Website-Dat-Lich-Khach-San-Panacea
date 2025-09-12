// Admin Reports JavaScript
// Quản lý báo cáo cho admin

// Khởi tạo biến toàn cục
let currentReportData = [];
let currentChart = null;

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

// Hiển thị loading
function showLoading(show = true) {
    const loader = document.getElementById('loader');
    if (loader) {
        loader.style.display = show ? 'flex' : 'none';
    }
}

// Tạo báo cáo
function generateReport(reportType) {
    showLoading(true);
    
    const startDate = document.getElementById('startDate')?.value;
    const endDate = document.getElementById('endDate')?.value;
    
    if (!startDate || !endDate) {
        showNotification('Vui lòng chọn khoảng thời gian', 'error');
        showLoading(false);
        return;
    }
    
    const params = new URLSearchParams({
        type: reportType,
        startDate: startDate,
        endDate: endDate
    });
    
    fetch(`/api/reports?${params}`)
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Không thể tạo báo cáo');
        })
        .then(data => {
            currentReportData = data;
            displayReport(data, reportType);
            showNotification('Tạo báo cáo thành công', 'success');
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification('Có lỗi xảy ra khi tạo báo cáo', 'error');
        })
        .finally(() => {
            showLoading(false);
        });
}

// Hiển thị báo cáo
function displayReport(data, reportType) {
    const reportContainer = document.getElementById('reportContainer');
    if (!reportContainer) return;
    
    let html = '';
    
    switch (reportType) {
        case 'inventory':
            html = generateInventoryReport(data);
            break;
        case 'transactions':
            html = generateTransactionReport(data);
            break;
        case 'summary':
            html = generateSummaryReport(data);
            break;
        default:
            html = '<p>Loại báo cáo không hợp lệ</p>';
    }
    
    reportContainer.innerHTML = html;
}

// Tạo báo cáo tồn kho
function generateInventoryReport(data) {
    if (!data || !data.length) {
        return '<p>Không có dữ liệu tồn kho</p>';
    }
    
    let html = `
        <div class="report-section">
            <h3>Báo cáo tồn kho</h3>
            <table class="table">
                <thead>
                    <tr>
                        <th>Mã vật phẩm</th>
                        <th>Tên vật phẩm</th>
                        <th>Loại</th>
                        <th>Số lượng tồn</th>
                        <th>Đơn giá</th>
                        <th>Tổng giá trị</th>
                    </tr>
                </thead>
                <tbody>
    `;
    
    let totalValue = 0;
    
    data.forEach(item => {
        const itemValue = item.soLuongTonKho * (item.donGia || 0);
        totalValue += itemValue;
        
        html += `
            <tr>
                <td>${item.maVatPham}</td>
                <td>${item.tenVatPham}</td>
                <td>${item.loaiVatPham}</td>
                <td>${item.soLuongTonKho}</td>
                <td>${(item.donGia || 0).toLocaleString('vi-VN')} VNĐ</td>
                <td>${itemValue.toLocaleString('vi-VN')} VNĐ</td>
            </tr>
        `;
    });
    
    html += `
                </tbody>
                <tfoot>
                    <tr>
                        <th colspan="5">Tổng giá trị tồn kho:</th>
                        <th>${totalValue.toLocaleString('vi-VN')} VNĐ</th>
                    </tr>
                </tfoot>
            </table>
        </div>
    `;
    
    return html;
}

// Tạo báo cáo giao dịch
function generateTransactionReport(data) {
    if (!data || !data.length) {
        return '<p>Không có dữ liệu giao dịch</p>';
    }
    
    let html = `
        <div class="report-section">
            <h3>Báo cáo giao dịch</h3>
            <table class="table">
                <thead>
                    <tr>
                        <th>Mã giao dịch</th>
                        <th>Loại</th>
                        <th>Vật phẩm</th>
                        <th>Số lượng</th>
                        <th>Ngày giao dịch</th>
                        <th>Người thực hiện</th>
                        <th>Trạng thái</th>
                    </tr>
                </thead>
                <tbody>
    `;
    
    data.forEach(transaction => {
        html += `
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
        `;
    });
    
    html += `
                </tbody>
            </table>
        </div>
    `;
    
    return html;
}

// Tạo báo cáo tổng hợp
function generateSummaryReport(data) {
    let html = `
        <div class="report-section">
            <h3>Báo cáo tổng hợp</h3>
            <div class="summary-stats">
                <div class="stat-card">
                    <h4>Tổng số vật phẩm</h4>
                    <p class="stat-number">${data.totalItems || 0}</p>
                </div>
                <div class="stat-card">
                    <h4>Tổng giao dịch</h4>
                    <p class="stat-number">${data.totalTransactions || 0}</p>
                </div>
                <div class="stat-card">
                    <h4>Giao dịch chờ duyệt</h4>
                    <p class="stat-number">${data.pendingTransactions || 0}</p>
                </div>
                <div class="stat-card">
                    <h4>Tổng giá trị kho</h4>
                    <p class="stat-number">${(data.totalValue || 0).toLocaleString('vi-VN')} VNĐ</p>
                </div>
            </div>
        </div>
    `;
    
    return html;
}

// Xuất báo cáo
function exportReport(format) {
    if (!currentReportData || !currentReportData.length) {
        showNotification('Không có dữ liệu để xuất', 'error');
        return;
    }
    
    showLoading(true);
    
    const params = new URLSearchParams({
        format: format,
        data: JSON.stringify(currentReportData)
    });
    
    fetch('/api/reports/export', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params
    })
    .then(response => {
        if (response.ok) {
            return response.blob();
        }
        throw new Error('Không thể xuất báo cáo');
    })
    .then(blob => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `report_${new Date().getTime()}.${format}`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        showNotification('Xuất báo cáo thành công', 'success');
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Có lỗi xảy ra khi xuất báo cáo', 'error');
    })
    .finally(() => {
        showLoading(false);
    });
}

// Khởi tạo khi trang được tải
document.addEventListener('DOMContentLoaded', function() {
    // Thiết lập ngày mặc định
    const today = new Date();
    const lastMonth = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());
    
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    
    if (startDateInput) {
        startDateInput.value = lastMonth.toISOString().split('T')[0];
    }
    
    if (endDateInput) {
        endDateInput.value = today.toISOString().split('T')[0];
    }
    
    // Xử lý các nút báo cáo
    const reportButtons = document.querySelectorAll('[data-report-type]');
    reportButtons.forEach(button => {
        button.addEventListener('click', function() {
            const reportType = this.getAttribute('data-report-type');
            generateReport(reportType);
        });
    });
    
    // Xử lý các nút xuất báo cáo
    const exportButtons = document.querySelectorAll('[data-export-format]');
    exportButtons.forEach(button => {
        button.addEventListener('click', function() {
            const format = this.getAttribute('data-export-format');
            exportReport(format);
        });
    });
});
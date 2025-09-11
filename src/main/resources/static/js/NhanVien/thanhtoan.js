// JavaScript cho chức năng thanh toán nhân viên

$(document).ready(function() {
    // Khởi tạo các event handlers
    initializeEventHandlers();
    
    // Load dữ liệu ban đầu
    loadInitialData();
});

function initializeEventHandlers() {
    // Xử lý submit form tạo thanh toán
    $('#paymentForm').on('submit', function(e) {
        e.preventDefault();
        createPayment();
    });
    
    // Xử lý click nút xác nhận thanh toán
    $(document).on('click', '.btn-confirm-payment', function() {
        const paymentId = $(this).data('payment-id');
        confirmPayment(paymentId);
    });
    
    // Xử lý click nút in hóa đơn
    $(document).on('click', '.btn-print-invoice', function() {
        const paymentId = $(this).data('payment-id');
        printInvoice(paymentId);
    });
    
    // Xử lý click nút xem chi tiết
    $(document).on('click', '.btn-view-detail', function() {
        const paymentId = $(this).data('payment-id');
        viewPaymentDetail(paymentId);
    });
    
    // Validation real-time cho form
    $('#bookingId').on('change', function() {
        validateBookingId($(this).val());
    });
    
    $('#soTien').on('input', function() {
        validateAmount($(this).val());
    });
}

function loadInitialData() {
    // Không cần refresh ngay khi load trang vì dữ liệu đã được render từ server
    // refreshPaymentTable(); // Bỏ comment này để tránh vòng lặp reload
    console.log('Trang thanh toán đã được khởi tạo');
}

// Cập nhật thông tin chi tiết thanh toán khi chọn booking
function updatePaymentInfo() {
    const bookingSelect = document.getElementById('bookingId');
    const selectedOption = bookingSelect.options[bookingSelect.selectedIndex];
    const paymentDetails = document.getElementById('paymentDetails');
    
    if (selectedOption.value === '' || selectedOption.disabled) {
        paymentDetails.style.display = 'none';
        return;
    }
    
    // Lấy dữ liệu từ data attributes
    const roomTotal = parseFloat(selectedOption.getAttribute('data-room-total')) || 0;
    const serviceTotal = parseFloat(selectedOption.getAttribute('data-service-total')) || 0;
    const deposit = parseFloat(selectedOption.getAttribute('data-deposit')) || 0;
    const paymentTotal = parseFloat(selectedOption.getAttribute('data-payment-total')) || 0;
    
    // Cập nhật hiển thị
    document.getElementById('roomTotal').textContent = formatCurrency(roomTotal);
    document.getElementById('serviceTotal').textContent = formatCurrency(serviceTotal);
    document.getElementById('depositAmount').textContent = formatCurrency(deposit);
    document.getElementById('totalPayment').textContent = formatCurrency(paymentTotal);
    
    // Tự động điền số tiền cần thanh toán và không cho phép thay đổi
    const soTienInput = document.getElementById('soTien');
    soTienInput.value = paymentTotal;
    soTienInput.setAttribute('readonly', true);
    soTienInput.style.backgroundColor = '#f3f4f6';
    soTienInput.style.cursor = 'not-allowed';
    
    // Hiển thị phần chi tiết
    paymentDetails.style.display = 'block';
}

// Format số tiền thành định dạng VND
function formatCurrency(amount) {
    if (isNaN(amount) || amount === null || amount === undefined) {
        return '0 VND';
    }
    return new Intl.NumberFormat('vi-VN').format(amount) + ' VND';
}

// Tạo thanh toán mới
function createPayment() {
    const formData = {
        bookingId: $('#bookingId').val(),
        soTien: $('#soTien').val(),
        phuongThuc: $('#phuongThuc').val(),
        noiDung: $('#noiDung').val(),
        maGiaoDich: $('#maGiaoDich').val()
    };
    
    // Validate dữ liệu
    if (!validatePaymentForm(formData)) {
        return;
    }
    
    // Hiển thị loading
    showLoading('Đang tạo thanh toán...');
    
    $.ajax({
        url: '/nhanvien/thanhtoan/create',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(formData),
        success: function(response) {
            hideLoading();
            if (response.success) {
                showSuccess('Tạo thanh toán thành công!');
                resetForm();
                refreshPaymentTable();
            } else {
                showError(response.message || 'Có lỗi xảy ra');
            }
        },
        error: function(xhr) {
            hideLoading();
            const errorMsg = xhr.responseJSON?.message || 'Lỗi kết nối server';
            showError(errorMsg);
        }
    });
}

// Xác nhận thanh toán
function confirmPayment(paymentId) {
    if (!confirm('Bạn có chắc chắn muốn xác nhận thanh toán này?')) {
        return;
    }
    
    showLoading('Đang xác nhận thanh toán...');
    
    $.ajax({
        url: `/nhanvien/thanhtoan/confirm/${paymentId}`,
        type: 'PUT',
        success: function(response) {
            hideLoading();
            if (response.success) {
                showSuccess('Xác nhận thanh toán thành công!');
                refreshPaymentTable();
            } else {
                showError(response.message || 'Có lỗi xảy ra');
            }
        },
        error: function(xhr) {
            hideLoading();
            const errorMsg = xhr.responseJSON?.message || 'Lỗi kết nối server';
            showError(errorMsg);
        }
    });
}

// In hóa đơn
function printInvoice(paymentId) {
    showLoading('Đang tạo hóa đơn...');
    
    $.ajax({
        url: `/nhanvien/thanhtoan/invoice/${paymentId}`,
        type: 'GET',
        success: function(response) {
            hideLoading();
            if (response.success) {
                // Hiển thị hóa đơn trong modal hoặc cửa sổ mới
                showInvoiceModal(response.invoice);
            } else {
                showError(response.message || 'Không thể tạo hóa đơn');
            }
        },
        error: function(xhr) {
            hideLoading();
            const errorMsg = xhr.responseJSON?.message || 'Lỗi kết nối server';
            showError(errorMsg);
        }
    });
}

// Xem chi tiết thanh toán
function viewPaymentDetail(paymentId) {
    showLoading('Đang tải chi tiết...');
    
    $.ajax({
        url: `/nhanvien/thanhtoan/detail/${paymentId}`,
        type: 'GET',
        success: function(payment) {
            hideLoading();
            showPaymentDetailModal(payment);
        },
        error: function(xhr) {
            hideLoading();
            const errorMsg = xhr.responseJSON?.message || 'Lỗi kết nối server';
            showError(errorMsg);
        }
    });
}

// Validation functions
function validatePaymentForm(data) {
    if (!data.bookingId) {
        showError('Vui lòng chọn booking');
        $('#bookingId').focus();
        return false;
    }
    
    if (!data.soTien || parseFloat(data.soTien) <= 0) {
        showError('Vui lòng nhập số tiền hợp lệ');
        $('#soTien').focus();
        return false;
    }
    
    if (!data.phuongThuc) {
        showError('Vui lòng chọn phương thức thanh toán');
        $('#phuongThuc').focus();
        return false;
    }
    
    return true;
}

function validateBookingId(bookingId) {
    if (bookingId) {
        // Có thể thêm validation AJAX để kiểm tra booking có tồn tại không
        $('#bookingId').removeClass('is-invalid').addClass('is-valid');
    } else {
        $('#bookingId').removeClass('is-valid').addClass('is-invalid');
    }
}

function validateAmount(amount) {
    const numAmount = parseFloat(amount);
    if (amount && numAmount > 0) {
        $('#soTien').removeClass('is-invalid').addClass('is-valid');
    } else {
        $('#soTien').removeClass('is-valid').addClass('is-invalid');
    }
}

// UI Helper functions
function showLoading(message) {
    // Hiển thị loading spinner với message
    if (!$('#loadingModal').length) {
        $('body').append(`
            <div class="modal fade" id="loadingModal" tabindex="-1" data-backdrop="static">
                <div class="modal-dialog modal-sm">
                    <div class="modal-content">
                        <div class="modal-body text-center">
                            <div class="spinner-border text-primary" role="status"></div>
                            <p class="mt-2 mb-0" id="loadingMessage">${message}</p>
                        </div>
                    </div>
                </div>
            </div>
        `);
    } else {
        $('#loadingMessage').text(message);
    }
    $('#loadingModal').modal('show');
}

function hideLoading() {
    $('#loadingModal').modal('hide');
}

function showSuccess(message) {
    toastr.success(message);
}

function showError(message) {
    toastr.error(message);
}

function showInfo(message) {
    toastr.info(message);
}

function resetForm() {
    $('#paymentForm')[0].reset();
    $('#paymentForm .form-control').removeClass('is-valid is-invalid');
}

function refreshPaymentTable() {
    // Reload trang để cập nhật bảng thanh toán
    location.reload();
}

// Modal functions
function showInvoiceModal(invoiceContent) {
    const modalHtml = `
        <div class="modal fade" id="invoiceModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Hóa đơn thanh toán</h5>
                        <button type="button" class="close" data-dismiss="modal">
                            <span>&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <pre style="white-space: pre-wrap; font-family: monospace;">${invoiceContent}</pre>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" onclick="printInvoiceContent()">In hóa đơn</button>
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Đóng</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Remove existing modal if any
    $('#invoiceModal').remove();
    
    // Add new modal
    $('body').append(modalHtml);
    $('#invoiceModal').modal('show');
}

function showPaymentDetailModal(payment) {
    const modalHtml = `
        <div class="modal fade" id="paymentDetailModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Chi tiết thanh toán #${payment.id}</h5>
                        <button type="button" class="close" data-dismiss="modal">
                            <span>&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-sm-4"><strong>Mã booking:</strong></div>
                            <div class="col-sm-8">${payment.booking?.id || 'N/A'}</div>
                        </div>
                        <div class="row mt-2">
                            <div class="col-sm-4"><strong>Số tiền:</strong></div>
                            <div class="col-sm-8">${formatCurrency(payment.soTien)}</div>
                        </div>
                        <div class="row mt-2">
                            <div class="col-sm-4"><strong>Phương thức:</strong></div>
                            <div class="col-sm-8">${payment.phuongThuc}</div>
                        </div>
                        <div class="row mt-2">
                            <div class="col-sm-4"><strong>Trạng thái:</strong></div>
                            <div class="col-sm-8"><span class="badge badge-${getStatusBadgeClass(payment.trangThai)}">${payment.trangThai}</span></div>
                        </div>
                        <div class="row mt-2">
                            <div class="col-sm-4"><strong>Nội dung:</strong></div>
                            <div class="col-sm-8">${payment.noiDung || 'Không có'}</div>
                        </div>
                        <div class="row mt-2">
                            <div class="col-sm-4"><strong>Ngày tạo:</strong></div>
                            <div class="col-sm-8">${formatDateTime(payment.ngayTao)}</div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Đóng</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Remove existing modal if any
    $('#paymentDetailModal').remove();
    
    // Add new modal
    $('body').append(modalHtml);
    $('#paymentDetailModal').modal('show');
}

function printInvoiceContent() {
    const content = $('#invoiceModal pre').text();
    const printWindow = window.open('', '_blank');
    printWindow.document.write(`
        <html>
            <head><title>Hóa đơn thanh toán</title></head>
            <body style="font-family: monospace; white-space: pre-wrap;">
                ${content}
            </body>
        </html>
    `);
    printWindow.document.close();
    printWindow.print();
}

// Utility functions
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

function formatDateTime(dateTime) {
    if (!dateTime) return 'N/A';
    return new Date(dateTime).toLocaleString('vi-VN');
}

function getStatusBadgeClass(status) {
    switch(status) {
        case 'THANH_CONG': return 'success';
        case 'CHO_XAC_NHAN': return 'warning';
        case 'THAT_BAI': return 'danger';
        case 'HUY': return 'secondary';
        default: return 'info';
    }
}

// Cấu hình toastr
if (typeof toastr !== 'undefined') {
    toastr.options = {
        "closeButton": true,
        "debug": false,
        "newestOnTop": true,
        "progressBar": true,
        "positionClass": "toast-top-right",
        "preventDuplicates": false,
        "onclick": null,
        "showDuration": "300",
        "hideDuration": "1000",
        "timeOut": "5000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    };
}
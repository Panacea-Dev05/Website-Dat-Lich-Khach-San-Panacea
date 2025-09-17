// Mobile sidebar toggle và khởi tạo các chức năng quản lý khách hàng
document.addEventListener('DOMContentLoaded', function() {
  // Mobile sidebar toggle
  const mobileToggle = document.getElementById('mobileToggle');
  const sidebar = document.getElementById('sidebar');
  
  if (mobileToggle && sidebar) {
    mobileToggle.addEventListener('click', function() {
      sidebar.classList.toggle('open');
    });
  }

  // Khởi tạo các chức năng quản lý khách hàng
  initCustomerManagement();
});

/**
 * Khởi tạo các chức năng quản lý khách hàng
 */
function initCustomerManagement() {
  // Gắn sự kiện cho form thêm/sửa khách hàng
  const customerForm = document.getElementById('customerForm');
  if (customerForm) {
    customerForm.addEventListener('submit', handleCustomerFormSubmit);
  }
  
  // Gắn sự kiện cho nút thêm khách hàng
  const btnAddCustomer = document.getElementById('btnAddCustomer');
  if (btnAddCustomer) {
    btnAddCustomer.addEventListener('click', showAddCustomerForm);
  }
  
  // Gắn sự kiện cho nút hủy
  const cancelBtn = document.getElementById('cancelBtn');
  if (cancelBtn) {
    cancelBtn.addEventListener('click', hideCustomerForm);
  }
}


/**
 * Hiển thị form thêm khách hàng mới
 */
function showAddCustomerForm() {
  var modal = document.getElementById('customerModal');
  var form = document.getElementById('customerForm');
  modal.style.display = 'flex';
  form.reset();
  document.getElementById('formTitle').innerText = 'Thêm khách hàng mới';
  document.getElementById('customerId').value = '';
  form.setAttribute('data-mode', 'add');
  clearFormError();
}

/**
 * Ẩn form khách hàng
 */
function hideCustomerForm() {
  document.getElementById('customerModal').style.display = 'none';
}

/**
 * Xử lý sự kiện submit form thêm/sửa khách hàng
 * @param {Event} event - Sự kiện submit form
 */
function handleCustomerFormSubmit(event) {
  event.preventDefault();
  clearFormError();
  
  const form = event.target;
  const customerId = document.getElementById('customerId').value;
  const customerName = document.getElementById('customerName').value;
  const customerEmail = document.getElementById('customerEmail').value;
  const customerPhone = document.getElementById('customerPhone').value;
  const customerAddress = document.getElementById('customerAddress').value;
  const customerStatus = document.getElementById('customerStatus').value;
  
  // Tách họ và tên
  const nameParts = customerName.trim().split(' ');
  const ten = nameParts.pop(); // Phần tử cuối cùng là tên
  const ho = nameParts.join(' '); // Các phần tử còn lại là họ
  
  const customerData = {
    ho: ho,
    ten: ten,
    email: customerEmail,
    soDienThoai: customerPhone,
    diaChi: customerAddress,
    trangThai: customerStatus
  };
  
  // Xác định phương thức HTTP (POST cho thêm mới, PUT cho cập nhật)
  const isEdit = customerId && customerId.trim() !== '';
  const method = isEdit ? 'PUT' : 'POST';
  const url = isEdit ? `/admin/customers/${customerId}` : '/admin/customers';
  
  // Gửi yêu cầu API
  fetch(url, {
    method: method,
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(customerData)
  })
  .then(response => {
    if (!response.ok) {
      if (response.status === 500) {
        throw new Error('Đã xảy ra lỗi hệ thống, vui lòng thử lại sau');
      }
      return response.json().then(data => {
        throw new Error(data.message || 'Có lỗi xảy ra khi lưu khách hàng');
      }).catch(err => {
        // Xử lý trường hợp response không phải JSON
        throw new Error('Có lỗi xảy ra khi lưu khách hàng');
      });
    }
    return response.json();
  }).catch(error => {
    console.error('Lỗi hệ thống:', error);
    showFormError(error.message || 'Có lỗi xảy ra khi lưu khách hàng');
    throw error;
  })
  .then(data => {
    // Hiển thị thông báo thành công
    alert(isEdit ? 'Cập nhật khách hàng thành công!' : 'Thêm khách hàng mới thành công!');
    
    // Đóng modal và tải lại trang
    document.getElementById('customerModal').style.display = 'none';
    window.location.reload();
  })
  .catch(error => {
    // Đã hiển thị lỗi ở trên, không cần alert lại
  });
}

/**
 * Hiển thị form chỉnh sửa khách hàng
 * @param {number} customerId - ID của khách hàng cần chỉnh sửa
 */
function editCustomer(customerId) {
  // Lấy thông tin khách hàng từ API
  fetch(`/admin/customers/${customerId}`)
    .then(response => {
      if (!response.ok) {
        if (response.status === 500) {
          throw new Error('Đã xảy ra lỗi hệ thống, vui lòng thử lại sau');
        }
        if (response.status === 404) {
          throw new Error('Không tìm thấy thông tin khách hàng');
        }
        throw new Error('Không thể tải thông tin khách hàng');
      }
      return response.json();
    })
    .then(data => {
      // Điền thông tin vào form
      document.getElementById('customerId').value = data.id;
      document.getElementById('customerName').value = `${data.ho} ${data.ten}`.trim();
      document.getElementById('customerEmail').value = data.email;
      document.getElementById('customerPhone').value = data.soDienThoai || '';
      document.getElementById('customerAddress').value = data.diaChi || '';
      document.getElementById('customerStatus').value = data.trangThai;
      
      // Cập nhật tiêu đề form và hiển thị
      document.getElementById('formTitle').innerText = 'Chỉnh sửa khách hàng';
      document.getElementById('customerForm').setAttribute('data-mode', 'edit');
      document.getElementById('customerModal').style.display = 'flex';
    })
    .catch(error => {
      console.error('Lỗi:', error);
      alert(error.message);
    });
}

/**
 * Xóa khách hàng
 * @param {number} customerId - ID của khách hàng cần xóa
 */
function deleteCustomer(customerId) {
  // Hiển thị hộp thoại xác nhận
  if (!confirm('Bạn có chắc chắn muốn xóa khách hàng này không?')) {
    return;
  }
  
  // Gửi yêu cầu xóa đến API
  fetch(`/admin/customers/${customerId}`, {
    method: 'DELETE'
  })
  .then(response => {
    if (!response.ok) {
      if (response.status === 500) {
        throw new Error('Đã xảy ra lỗi hệ thống, vui lòng thử lại sau');
      }
      if (response.status === 404) {
        throw new Error('Không tìm thấy khách hàng');
      }
      return response.json().then(data => {
        throw new Error(data.message || 'Có lỗi xảy ra khi xóa khách hàng');
      }).catch(err => {
        // Xử lý trường hợp response không phải JSON
        throw new Error('Có lỗi xảy ra khi xóa khách hàng');
      });
    }
    
    // Hiển thị thông báo thành công và tải lại trang
    alert('Xóa khách hàng thành công!');
    window.location.reload();
  })
  .catch(error => {
    console.error('Lỗi:', error);
    alert(error.message || 'Có lỗi xảy ra khi xóa khách hàng');
  });
}

/**
 * Hiển thị chi tiết khách hàng
 * @param {number} customerId - ID của khách hàng cần xem chi tiết
 */
function viewCustomer(customerId) {
  fetch('/admin/customers/' + customerId)
    .then(res => {
      if (!res.ok) {
        if (res.status === 500) {
          throw new Error('Đã xảy ra lỗi hệ thống, vui lòng thử lại sau');
        }
        if (res.status === 404) {
          throw new Error('Không tìm thấy thông tin khách hàng');
        }
        throw new Error('Lỗi khi tải dữ liệu khách hàng');
      }
      return res.json();
    })
    .then(data => {
      // Kiểm tra nếu có lỗi từ server
      if (data.error) {
        throw new Error(data.message || 'Lỗi không xác định');
      }
      
      // Nếu không có lỗi, hiển thị thông tin khách hàng
      document.getElementById('viewCustomerId').textContent = data.id;
      document.getElementById('viewCustomerName').textContent = data.ho + ' ' + data.ten;
      document.getElementById('viewCustomerEmail').textContent = data.email;
      document.getElementById('viewCustomerPhone').textContent = data.soDienThoai || 'Chưa cập nhật';
      document.getElementById('viewCustomerAddress').textContent = data.diaChi || 'Chưa cập nhật';
      document.getElementById('viewCustomerPoints').textContent = data.diemTichLuy || '0';
      
      // Hiển thị trạng thái với màu sắc tương ứng
      const statusElement = document.getElementById('viewCustomerStatus');
      statusElement.textContent = data.trangThai === 'HOAT_DONG' ? 'Hoạt động' : 'Khóa';
      statusElement.className = data.trangThai === 'HOAT_DONG' ? 'status-active' : 'status-locked';
      
      // Hiển thị ngày tạo
      const createdDate = data.createdDate ? new Date(data.createdDate).toLocaleDateString('vi-VN') : 'Không có thông tin';
      document.getElementById('viewCustomerCreatedDate').textContent = createdDate;
      
      // Hiển thị modal
      document.getElementById('viewCustomerModal').style.display = 'flex';
      
      // Lưu ID khách hàng cho nút chỉnh sửa
      document.getElementById('editCustomerBtn').setAttribute('data-id', data.id);
    })
    .catch(error => {
      console.error('Lỗi:', error);
      alert('Không thể xem chi tiết khách hàng: ' + error.message);
    });
}

function showFormError(message) {
  const errorDiv = document.getElementById('customerFormError');
  if (errorDiv) {
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
  }
}
function clearFormError() {
  const errorDiv = document.getElementById('customerFormError');
  if (errorDiv) {
    errorDiv.textContent = '';
    errorDiv.style.display = 'none';
  }
}

// Các hàm này đã được định nghĩa ở trên, nên không cần định nghĩa lại
// Xem các hàm editCustomer, hideCustomerForm và deleteCustomer ở phần trên của file

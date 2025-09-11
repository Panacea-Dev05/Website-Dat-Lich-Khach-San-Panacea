document.addEventListener("DOMContentLoaded", () => {
  // Sidebar toggle for mobile
  const mobileToggle = document.getElementById("mobileToggle");
  const sidebar = document.getElementById("sidebar");

  if (mobileToggle && sidebar) {
    mobileToggle.addEventListener("click", () => {
      sidebar.classList.toggle("open");
    });
  }

  // Thêm các chức năng tìm kiếm và lọc
  const searchInput = document.createElement('input');
  searchInput.type = 'text';
  searchInput.placeholder = 'Tìm kiếm thanh toán...';
  searchInput.className = 'search-input';
  searchInput.style.cssText = 'padding: 10px 16px; border-radius: 8px; border: 1px solid #d1fae5; margin-bottom: 16px; width: 300px;';
  
  const filterSelect = document.createElement('select');
  filterSelect.className = 'filter-select';
  filterSelect.style.cssText = 'padding: 10px 16px; border-radius: 8px; border: 1px solid #d1fae5; margin-bottom: 16px; margin-left: 16px; width: 200px;';
  
  const filterOptions = [
    { value: '', text: 'Tất cả trạng thái' },
    { value: 'Completed', text: 'Hoàn thành' },
    { value: 'Pending', text: 'Chờ xử lý' },
    { value: 'Failed', text: 'Thất bại' },
    { value: 'Refunded', text: 'Hoàn tiền' }
  ];
  
  filterOptions.forEach(option => {
    const optionElement = document.createElement('option');
    optionElement.value = option.value;
    optionElement.textContent = option.text;
    filterSelect.appendChild(optionElement);
  });
  
  // Thêm các phần tử vào DOM
  const btnAddPayment = document.getElementById('btnAddPayment');
  if (btnAddPayment) {
    const filterContainer = document.createElement('div');
    filterContainer.style.cssText = 'display: flex; align-items: center; margin-bottom: 16px;';
    filterContainer.appendChild(searchInput);
    filterContainer.appendChild(filterSelect);
    
    btnAddPayment.parentNode.insertBefore(filterContainer, btnAddPayment.nextSibling);
  }
  
  // Xử lý nút tạo hoàn tiền
  const btnCreateRefund = document.getElementById('btnCreateRefund');
  const refundModal = document.getElementById('refundModal');
  const closeRefundModal = document.getElementById('closeRefundModal');
  const cancelRefundBtn = document.getElementById('cancelRefundBtn');
  const refundForm = document.getElementById('refundForm');
  
  if (btnCreateRefund) {
    btnCreateRefund.addEventListener('click', () => {
      refundModal.style.display = 'flex';
      refundForm.reset();
    });
  }
  
  if (closeRefundModal) {
    closeRefundModal.addEventListener('click', () => {
      refundModal.style.display = 'none';
    });
  }
  
  if (cancelRefundBtn) {
    cancelRefundBtn.addEventListener('click', () => {
      refundModal.style.display = 'none';
    });
  }
  
  // Xử lý submit form hoàn tiền
  if (refundForm) {
    refundForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      
      const formData = new FormData(refundForm);
      const refundData = {
        bookingId: formData.get('refundBookingId'),
        refundAmount: formData.get('refundAmount'),
        reason: formData.get('refundReason') || 'Hoàn tiền do hủy đặt phòng'
      };
      
      try {
        const response = await fetch('/admin/payments/refund', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(refundData)
        });
        
        if (response.ok) {
          const result = await response.json();
          if (result) {
            alert('Tạo hoàn tiền thành công!');
            refundModal.style.display = 'none';
            location.reload(); // Reload để cập nhật danh sách
          } else {
            alert('Không thể tạo hoàn tiền. Vui lòng kiểm tra lại thông tin.');
          }
        } else {
          alert('Lỗi khi tạo hoàn tiền.');
        }
      } catch (error) {
        console.error('Error:', error);
        alert('Lỗi kết nối. Vui lòng thử lại.');
      }
    });
  }

  // Xử lý tìm kiếm và lọc
  function filterTable() {
    const searchTerm = searchInput.value.toLowerCase();
    const filterValue = filterSelect.value;
    const rows = document.querySelectorAll('#paymentTableBody tr');
    
    rows.forEach(row => {
      const paymentId = row.querySelector('td:nth-child(2)').textContent.toLowerCase();
      const bookingId = row.querySelector('td:nth-child(3)').textContent.toLowerCase();
      const customerName = row.querySelector('td:nth-child(4)').textContent.toLowerCase();
      const statusElement = row.querySelector('td:nth-child(8) span');
      const statusText = statusElement ? statusElement.textContent.toLowerCase() : '';
      const statusClass = statusElement ? statusElement.className : '';
      
      const matchesSearch = paymentId.includes(searchTerm) || 
                           bookingId.includes(searchTerm) || 
                           customerName.includes(searchTerm);
      
      const matchesFilter = !filterValue || 
                           (filterValue === 'Completed' && statusClass.includes('completed')) ||
                           (filterValue === 'Pending' && statusClass.includes('pending')) ||
                           (filterValue === 'Failed' && statusClass.includes('failed')) ||
                           (filterValue === 'Refunded' && statusClass.includes('refunded'));
      
      row.style.display = matchesSearch && matchesFilter ? '' : 'none';
    });
  }
  
  searchInput.addEventListener('input', filterTable);
  filterSelect.addEventListener('change', filterTable);
});

// Hàm định dạng tiền tệ
function formatCurrency(amount) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
}

// Hàm định dạng ngày giờ
function formatDateTime(dateTimeStr) {
  if (!dateTimeStr) return '';
  const date = new Date(dateTimeStr);
  return date.toLocaleString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
}

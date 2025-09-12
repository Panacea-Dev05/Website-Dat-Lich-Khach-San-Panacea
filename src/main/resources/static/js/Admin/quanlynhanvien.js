// Khai báo các biến toàn cục để sử dụng trong các hàm
let staffForm, staffFormOverlay, formTitle, btnAddStaff, cancelBtn, closeFormBtn, submitBtn, staffTableBody;

// Hàm mở form với hiệu ứng
function openStaffForm() {
  staffFormOverlay.style.display = 'flex';
  setTimeout(() => {
    staffFormOverlay.classList.add('active');
  }, 10);
}

// Hàm đóng form với hiệu ứng
function closeStaffForm() {
  // Ẩn lớp active trước, rồi mới ẩn overlay để mượt hơn
  if (staffFormOverlay) {
    staffFormOverlay.classList.remove('active');
    setTimeout(() => {
      staffFormOverlay.style.display = 'none';
    }, 300);
  }
}

document.addEventListener("DOMContentLoaded", () => {
  // Sidebar toggle for mobile
  const mobileToggle = document.getElementById("mobileToggle");
  const sidebar = document.getElementById("sidebar");

  if (mobileToggle && sidebar) {
    mobileToggle.addEventListener("click", () => {
      sidebar.classList.toggle("open");
    });
  }

  // Khởi tạo các phần tử DOM
  staffForm = document.getElementById('staffForm');
  staffFormOverlay = document.getElementById('staffFormOverlay');
  formTitle = document.getElementById('formTitle');
  btnAddStaff = document.getElementById('btnAddStaff');
  cancelBtn = document.getElementById('cancelBtn');
  closeFormBtn = document.getElementById('closeFormBtn');
  submitBtn = document.getElementById('submitBtn');
  staffTableBody = document.getElementById('staffTableBody');

  // Hiển thị form thêm nhân viên
  if (btnAddStaff) {
    btnAddStaff.addEventListener('click', () => {
      resetForm();
<<<<<<< HEAD
      if (formTitle) formTitle.textContent = 'Thêm nhân viên mới';
      openStaffForm();
=======
      formTitle.textContent = 'Thêm nhân viên mới';
      staffForm.style.display = 'grid';
>>>>>>> f4609f3f78b032a6efdb6d54d3371006ea29fbf5
    });
  }

  // Hủy thêm/sửa nhân viên
  if (cancelBtn) {
    cancelBtn.addEventListener('click', () => {
<<<<<<< HEAD
      closeStaffForm();
    });
  }
  
  // Đóng form khi click vào nút đóng
  if (closeFormBtn) {
    closeFormBtn.addEventListener('click', () => {
      closeStaffForm();
    });
  }
  
  // Đóng form khi click vào overlay
  if (staffFormOverlay) {
    staffFormOverlay.addEventListener('click', (e) => {
      if (e.target === staffFormOverlay) {
        closeStaffForm();
      }
    });
  }
  
=======
      staffForm.style.display = 'none';
    });
  }
>>>>>>> f4609f3f78b032a6efdb6d54d3371006ea29fbf5

  // Xử lý submit form
  if (staffForm) {
    staffForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      
      const staffId = document.getElementById('staffId').value;
      const staffData = {
        maNhanVien: document.getElementById('maNhanVien').value,
        ho: document.getElementById('ho').value,
        ten: document.getElementById('ten').value,
        email: document.getElementById('email').value,
        soDienThoai: document.getElementById('soDienThoai').value,
        chucVu: document.getElementById('chucVu').value,
        trangThai: document.getElementById('trangThai').value
      };
      
      try {
        let response;
        let staff;
        
        if (staffId) {
          // Cập nhật nhân viên
          response = await fetch(`/admin/staff/${staffId}`, {
            method: 'PUT',
<<<<<<< HEAD
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(staffData)
          });
=======
            headers: {
              'Content-Type': 'application/json'
            },
            body: JSON.stringify(staffData)
          });
          
          staff = await response.json();
          updateStaffRow(staff);
>>>>>>> f4609f3f78b032a6efdb6d54d3371006ea29fbf5
        } else {
          // Thêm nhân viên mới
          response = await fetch('/admin/staff', {
            method: 'POST',
<<<<<<< HEAD
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(staffData)
          });
        }
  
        if (!response.ok) {
          // Thử đọc thông tin lỗi từ server
          let errorText = 'Yêu cầu không thành công';
          try { const err = await response.json(); errorText = err.message || err.error || errorText; } catch (_) {}
          throw new Error(`${errorText} (HTTP ${response.status})`);
        }
  
        staff = await response.json();
  
        if (!staff || !staff.id) {
          throw new Error('Dữ liệu trả về không hợp lệ');
        }
  
        if (staffId) {
          updateStaffRow(staff);
          showNotification('Cập nhật nhân viên thành công');
        } else {
          addStaffRow(staff);
          showNotification('Thêm nhân viên thành công');
        }
  
        closeStaffForm();
      } catch (error) {
        console.error('Lỗi:', error);
        showNotification(error.message || 'Đã xảy ra lỗi, vui lòng thử lại', 'error');
=======
            headers: {
              'Content-Type': 'application/json'
            },
            body: JSON.stringify(staffData)
          });
          
          staff = await response.json();
          addStaffRow(staff);
        }
        
        staffForm.style.display = 'none';
        showNotification(staffId ? 'Cập nhật nhân viên thành công' : 'Thêm nhân viên thành công');
      } catch (error) {
        console.error('Lỗi:', error);
        showNotification('Có lỗi xảy ra', 'error');
>>>>>>> f4609f3f78b032a6efdb6d54d3371006ea29fbf5
      }
    });
  }
});

// Hàm reset form
function resetForm() {
  const form = document.getElementById('staffForm');
  if (form) {
    form.reset();
    document.getElementById('staffId').value = '';
  }
}

// Hàm thêm hàng nhân viên mới vào bảng
function addStaffRow(staff) {
  const staffTableBody = document.getElementById('staffTableBody');
  if (!staffTableBody) return;
  
  const rows = staffTableBody.querySelectorAll('tr');
  const newRow = document.createElement('tr');
  
  // Tính STT mới
  const newIndex = rows.length + 1;
  
  newRow.innerHTML = `
    <td>${newIndex}</td>
    <td>${staff.maNhanVien}</td>
    <td>${staff.ho} ${staff.ten}</td>
    <td>${staff.email}</td>
    <td>${staff.soDienThoai}</td>
    <td>${staff.chucVu}</td>
    <td>
      <span class="${staff.trangThai === 'HOAT_DONG' ? 'status-active' : 'status-inactive'}">
        ${staff.trangThai === 'HOAT_DONG' ? 'Hoạt động' : 'Nghỉ việc'}
      </span>
    </td>
    <td>
      <button class="btn-edit" onclick="editStaff(${staff.id})" title="Sửa">
        <span class="material-icons">edit</span>
      </button>
      <button class="btn-delete" onclick="deleteStaff(${staff.id})" title="Xóa">
        <span class="material-icons">delete</span>
      </button>
    </td>
  `;
  
  staffTableBody.appendChild(newRow);
}

// Hàm cập nhật hàng nhân viên trong bảng
function updateStaffRow(staff) {
  const staffTableBody = document.getElementById('staffTableBody');
  if (!staffTableBody) return;
  
  const rows = staffTableBody.querySelectorAll('tr');
  
  for (let i = 0; i < rows.length; i++) {
    const editBtn = rows[i].querySelector('.btn-edit');
    if (editBtn && editBtn.getAttribute('onclick').includes(staff.id)) {
      rows[i].cells[1].textContent = staff.maNhanVien;
      rows[i].cells[2].textContent = `${staff.ho} ${staff.ten}`;
      rows[i].cells[3].textContent = staff.email;
      rows[i].cells[4].textContent = staff.soDienThoai;
      rows[i].cells[5].textContent = staff.chucVu;
      
      const statusSpan = rows[i].cells[6].querySelector('span');
      statusSpan.className = staff.trangThai === 'HOAT_DONG' ? 'status-active' : 'status-inactive';
      statusSpan.textContent = staff.trangThai === 'HOAT_DONG' ? 'Hoạt động' : 'Nghỉ việc';
      
      break;
    }
  }
}

// Hàm chỉnh sửa nhân viên
async function editStaff(id) {
  try {
    const response = await fetch(`/admin/staff/${id}`);
    
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.error || `Lỗi HTTP: ${response.status} - ${response.statusText}`);
    }
    
    const staff = await response.json();
    
    // Kiểm tra dữ liệu trả về
    if (!staff || !staff.id) {
      throw new Error('Dữ liệu nhân viên không hợp lệ hoặc không tồn tại');
    }
    
    document.getElementById('staffId').value = staff.id;
    document.getElementById('maNhanVien').value = staff.maNhanVien || '';
    document.getElementById('ho').value = staff.ho || '';
    document.getElementById('ten').value = staff.ten || '';
    document.getElementById('email').value = staff.email || '';
    document.getElementById('soDienThoai').value = staff.soDienThoai || '';
    
    // Chọn chức vụ
    const chucVuSelect = document.getElementById('chucVu');
    let foundChucVu = false;
    for (let i = 0; i < chucVuSelect.options.length; i++) {
      if (chucVuSelect.options[i].value === staff.chucVu) {
        chucVuSelect.selectedIndex = i;
        foundChucVu = true;
        break;
      }
    }
    if (!foundChucVu && chucVuSelect.options.length > 0) {
      chucVuSelect.selectedIndex = 0;
    }
    
    // Chọn trạng thái
    const trangThaiSelect = document.getElementById('trangThai');
    let foundTrangThai = false;
    for (let i = 0; i < trangThaiSelect.options.length; i++) {
      if (trangThaiSelect.options[i].value === staff.trangThai) {
        trangThaiSelect.selectedIndex = i;
        foundTrangThai = true;
        break;
      }
    }
    if (!foundTrangThai && trangThaiSelect.options.length > 0) {
      trangThaiSelect.selectedIndex = 0;
    }
    
    document.getElementById('formTitle').textContent = 'Chỉnh sửa thông tin nhân viên';
    openStaffForm();
  } catch (error) {
    console.error('Lỗi khi tải thông tin nhân viên:', error);
    showNotification(`Không thể tải thông tin nhân viên: ${error.message}`, 'error');
  }
}

// Hàm xóa nhân viên
async function deleteStaff(id) {
  if (confirm('Bạn có chắc chắn muốn xóa nhân viên này?')) {
    try {
      const response = await fetch(`/admin/staff/${id}`, {
        method: 'DELETE'
      });
      
      if (response.ok) {
        const staffTableBody = document.getElementById('staffTableBody');
        const rows = staffTableBody.querySelectorAll('tr');
        
        for (let i = 0; i < rows.length; i++) {
          const deleteBtn = rows[i].querySelector('.btn-delete');
          if (deleteBtn && deleteBtn.getAttribute('onclick').includes(id)) {
            rows[i].remove();
            
            // Cập nhật lại STT
            const remainingRows = staffTableBody.querySelectorAll('tr');
            for (let j = 0; j < remainingRows.length; j++) {
              remainingRows[j].cells[0].textContent = j + 1;
            }
            
            break;
          }
        }
        
        showNotification('Xóa nhân viên thành công');
      } else {
        throw new Error('Không thể xóa nhân viên');
      }
    } catch (error) {
      console.error('Lỗi:', error);
      showNotification('Không thể xóa nhân viên', 'error');
    }
  }
}

// Hàm hiển thị thông báo
function showNotification(message, type = 'success') {
  const notification = document.createElement('div');
  notification.className = `notification ${type}`;
  notification.textContent = message;
  
  document.body.appendChild(notification);
  
  setTimeout(() => {
    notification.classList.add('show');
  }, 10);
  
  setTimeout(() => {
    notification.classList.remove('show');
    setTimeout(() => {
      notification.remove();
    }, 300);
  }, 3000);
}

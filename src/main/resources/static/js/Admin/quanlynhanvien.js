document.addEventListener("DOMContentLoaded", () => {
  // Sidebar toggle for mobile
  const mobileToggle = document.getElementById("mobileToggle");
  const sidebar = document.getElementById("sidebar");

  if (mobileToggle && sidebar) {
    mobileToggle.addEventListener("click", () => {
      sidebar.classList.toggle("open");
    });
  }

  // Các phần tử DOM
  const staffForm = document.getElementById('staffForm');
  const formTitle = document.getElementById('formTitle');
  const btnAddStaff = document.getElementById('btnAddStaff');
  const cancelBtn = document.getElementById('cancelBtn');
  const submitBtn = document.getElementById('submitBtn');
  const staffTableBody = document.getElementById('staffTableBody');

  // Hiển thị form thêm nhân viên
  btnAddStaff.addEventListener('click', () => {
    resetForm();
    formTitle.textContent = 'Thêm nhân viên mới';
    staffForm.style.display = 'grid';
  });

  // Hủy thêm/sửa nhân viên
  cancelBtn.addEventListener('click', () => {
    staffForm.style.display = 'none';
  });

  // Xử lý submit form
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
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(staffData)
        });
        
        staff = await response.json();
        updateStaffRow(staff);
      } else {
        // Thêm nhân viên mới
        response = await fetch('/admin/staff', {
          method: 'POST',
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
      showNotification('Đã xảy ra lỗi, vui lòng thử lại', 'error');
    }
  });
});

// Hàm reset form
function resetForm() {
  document.getElementById('staffId').value = '';
  document.getElementById('maNhanVien').value = '';
  document.getElementById('ho').value = '';
  document.getElementById('ten').value = '';
  document.getElementById('email').value = '';
  document.getElementById('soDienThoai').value = '';
  document.getElementById('chucVu').selectedIndex = 0;
  document.getElementById('trangThai').selectedIndex = 0;
}

// Hàm thêm hàng nhân viên mới vào bảng
function addStaffRow(staff) {
  const staffTableBody = document.getElementById('staffTableBody');
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

// Hàm sửa nhân viên
async function editStaff(id) {
  try {
    const response = await fetch(`/admin/staff/${id}`);
    const staff = await response.json();
    
    document.getElementById('staffId').value = staff.id;
    document.getElementById('maNhanVien').value = staff.maNhanVien;
    document.getElementById('ho').value = staff.ho;
    document.getElementById('ten').value = staff.ten;
    document.getElementById('email').value = staff.email;
    document.getElementById('soDienThoai').value = staff.soDienThoai;
    
    const chucVuSelect = document.getElementById('chucVu');
    for (let i = 0; i < chucVuSelect.options.length; i++) {
      if (chucVuSelect.options[i].value === staff.chucVu) {
        chucVuSelect.selectedIndex = i;
        break;
      }
    }
    
    const trangThaiSelect = document.getElementById('trangThai');
    for (let i = 0; i < trangThaiSelect.options.length; i++) {
      if (trangThaiSelect.options[i].value === staff.trangThai) {
        trangThaiSelect.selectedIndex = i;
        break;
      }
    }
    
    document.getElementById('formTitle').textContent = 'Sửa thông tin nhân viên';
    document.getElementById('staffForm').style.display = 'grid';
  } catch (error) {
    console.error('Lỗi:', error);
    showNotification('Không thể tải thông tin nhân viên', 'error');
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

document.addEventListener("DOMContentLoaded", () => {
  // Thêm các chức năng tìm kiếm và lọc
  const searchInput = document.createElement('input');
  searchInput.type = 'text';
  searchInput.placeholder = 'Tìm kiếm nhân viên...';
  searchInput.className = 'search-input';
  
  const filterSelect = document.createElement('select');
  filterSelect.className = 'filter-select';
  
  const filterOptions = [
    { value: '', text: 'Tất cả trạng thái' },
    { value: 'HOAT_DONG', text: 'Hoạt động' },
    { value: 'NGHI_VIEC', text: 'Nghỉ việc' }
  ];
  
  filterOptions.forEach(option => {
    const optionElement = document.createElement('option');
    optionElement.value = option.value;
    optionElement.textContent = option.text;
    filterSelect.appendChild(optionElement);
  });
  
  // Thêm các phần tử vào DOM
  const btnAddStaff = document.getElementById('btnAddStaff');
  if (btnAddStaff) {
    const filterContainer = document.createElement('div');
    filterContainer.className = 'filter-container';
    filterContainer.appendChild(searchInput);
    filterContainer.appendChild(filterSelect);
    
    btnAddStaff.parentNode.insertBefore(filterContainer, btnAddStaff.nextSibling);
  }
  
  // Xử lý tìm kiếm và lọc
  function filterTable() {
    const searchTerm = searchInput.value.toLowerCase();
    const filterValue = filterSelect.value;
    const rows = document.querySelectorAll('#staffTableBody tr');
    
    rows.forEach(row => {
      // Bỏ qua hàng thông báo nếu có
      if (row.cells.length === 1 && row.cells[0].colSpan > 1) {
        return;
      }
      
      const staffId = row.querySelector('td:nth-child(2)').textContent.toLowerCase();
      const staffName = row.querySelector('td:nth-child(3)').textContent.toLowerCase();
      const staffEmail = row.querySelector('td:nth-child(4)').textContent.toLowerCase();
      const staffPhone = row.querySelector('td:nth-child(5)').textContent.toLowerCase();
      const staffPosition = row.querySelector('td:nth-child(6)').textContent.toLowerCase();
      const statusElement = row.querySelector('td:nth-child(7) span');
      const statusClass = statusElement ? statusElement.className : '';
      
      const matchesSearch = staffId.includes(searchTerm) || 
                           staffName.includes(searchTerm) || 
                           staffEmail.includes(searchTerm) || 
                           staffPhone.includes(searchTerm) || 
                           staffPosition.includes(searchTerm);
      
      const matchesFilter = !filterValue || 
                           (filterValue === 'HOAT_DONG' && statusClass.includes('status-active')) ||
                           (filterValue === 'NGHI_VIEC' && statusClass.includes('status-inactive'));
      
      row.style.display = matchesSearch && matchesFilter ? '' : 'none';
    });
  }
  
  searchInput.addEventListener('input', filterTable);
  filterSelect.addEventListener('change', filterTable);
});
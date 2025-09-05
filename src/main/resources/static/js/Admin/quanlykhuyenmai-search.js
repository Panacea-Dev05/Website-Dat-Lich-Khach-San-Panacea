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
  searchInput.placeholder = 'Tìm kiếm khuyến mãi...';
  searchInput.className = 'search-input';
  
  const filterSelect = document.createElement('select');
  filterSelect.className = 'filter-select';
  
  const filterOptions = [
    { value: '', text: 'Tất cả trạng thái' },
    { value: 'HOAT_DONG', text: 'Hoạt động' },
    { value: 'TAM_NGUNG', text: 'Tạm ngưng' },
    { value: 'HET_HAN', text: 'Hết hạn' }
  ];
  
  filterOptions.forEach(option => {
    const optionElement = document.createElement('option');
    optionElement.value = option.value;
    optionElement.textContent = option.text;
    filterSelect.appendChild(optionElement);
  });
  
  // Thêm các phần tử vào DOM
  const btnAddPromotion = document.getElementById('btnAddPromotion');
  if (btnAddPromotion) {
    const filterContainer = document.createElement('div');
    filterContainer.className = 'filter-container';
    filterContainer.appendChild(searchInput);
    filterContainer.appendChild(filterSelect);
    
    btnAddPromotion.parentNode.insertBefore(filterContainer, btnAddPromotion.nextSibling);
  }
  
  // Xử lý tìm kiếm và lọc
  function filterTable() {
    const searchTerm = searchInput.value.toLowerCase();
    const filterValue = filterSelect.value;
    const rows = document.querySelectorAll('#promotionTableBody tr');
    
    rows.forEach(row => {
      // Bỏ qua hàng thông báo "Không có khuyến mãi nào"
      if (row.cells.length === 1 && row.cells[0].colSpan === 9) {
        return;
      }
      
      const promotionCode = row.querySelector('td:nth-child(2)').textContent.toLowerCase();
      const promotionName = row.querySelector('td:nth-child(3)').textContent.toLowerCase();
      const discountType = row.querySelector('td:nth-child(4)').textContent.toLowerCase();
      const statusElement = row.querySelector('td:nth-child(8) span');
      const statusClass = statusElement ? statusElement.className : '';
      
      const matchesSearch = promotionCode.includes(searchTerm) || 
                           promotionName.includes(searchTerm) || 
                           discountType.includes(searchTerm);
      
      const matchesFilter = !filterValue || 
                           (filterValue === 'HOAT_DONG' && statusClass.includes('status-active')) ||
                           (filterValue === 'TAM_NGUNG' && statusClass.includes('status-paused')) ||
                           (filterValue === 'HET_HAN' && statusClass.includes('status-expired'));
      
      row.style.display = matchesSearch && matchesFilter ? '' : 'none';
    });
  }
  
  searchInput.addEventListener('input', filterTable);
  filterSelect.addEventListener('change', filterTable);
});
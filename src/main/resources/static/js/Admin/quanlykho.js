// Form handling
document.getElementById('itemForm').addEventListener('submit', function(e) {
    e.preventDefault();
    // Handle item creation/update
    console.log('Saving item...');
});

document.getElementById('transactionForm').addEventListener('submit', function(e) {
    e.preventDefault();
    // Handle transaction
    console.log('Processing transaction...');
});

// Search and filter functions
function searchItems() {
    const searchTerm = document.getElementById('searchInput').value;
    const category = document.getElementById('categoryFilter').value;
    const status = document.getElementById('statusFilter').value;
    
    console.log('Searching items:', { searchTerm, category, status });
    // Implement search logic
}

function resetSearch() {
    document.getElementById('searchInput').value = '';
    document.getElementById('categoryFilter').value = '';
    document.getElementById('statusFilter').value = '';
    searchItems();
}

function resetForm() {
    document.getElementById('itemForm').reset();
}

function changePageSize() {
    const pageSize = document.getElementById('pageSize').value;
    console.log('Changing page size to:', pageSize);
    // Implement pagination
}

// Item actions
function editItem(id) {
    console.log('Editing item:', id);
    // Implement edit functionality
}

function deleteItem(id) {
    if (confirm('Bạn có chắc chắn muốn xóa vật phẩm này?')) {
        console.log('Deleting item:', id);
        // Implement delete functionality
    }
}

// Report functions
function exportTransactions() {
    console.log('Exporting transactions...');
    // Implement export functionality
}

function generateReport() {
    console.log('Generating report...');
    // Implement report generation
}

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    // Set current date for transaction form
    const today = new Date().toISOString().split('T')[0];
    document.querySelector('input[name="ngayGiaoDich"]').value = today;
});
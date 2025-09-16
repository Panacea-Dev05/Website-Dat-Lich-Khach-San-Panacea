// JavaScript cho trang báo cáo thống kê
document.addEventListener('DOMContentLoaded', function() {
    console.log('Trang báo cáo thống kê đã được tải');
    
    // Khởi tạo các biểu đồ
    initializeCharts();
    
    // Auto refresh dữ liệu mỗi 5 phút
    setInterval(function() {
        location.reload();
    }, 300000);
});

function initializeCharts() {
    // Kiểm tra xem Chart.js đã được tải chưa
    if (typeof Chart === 'undefined') {
        console.error('Chart.js chưa được tải');
        return;
    }
    
    console.log('Đang khởi tạo biểu đồ...');
    
    // Biểu đồ doanh thu
    const revenueCtx = document.getElementById('revenueChart');
    if (revenueCtx) {
        createRevenueChart(revenueCtx);
    }
    
    // Biểu đồ loại phòng
    const roomTypeCtx = document.getElementById('roomTypeChart');
    if (roomTypeCtx) {
        createRoomTypeChart(roomTypeCtx);
    }
}

function createRevenueChart(ctx) {
    // Dữ liệu mẫu cho biểu đồ doanh thu
    const revenueData = {
        labels: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6', 
                'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12'],
        datasets: [{
            label: 'Doanh thu (VNĐ)',
            data: [10000000, 12000000, 15000000, 18000000, 16000000, 20000000,
                   22000000, 19000000, 17000000, 21000000, 23000000, 25000000],
            backgroundColor: 'rgba(16, 185, 129, 0.2)',
            borderColor: 'rgba(16, 185, 129, 1)',
            borderWidth: 2,
            pointBackgroundColor: 'rgba(16, 185, 129, 1)',
            pointBorderColor: '#fff',
            pointHoverRadius: 6,
            pointHoverBackgroundColor: '#fff',
            pointHoverBorderColor: 'rgba(16, 185, 129, 1)',
            tension: 0.4,
            fill: true
        }]
    };
    
    new Chart(ctx, {
        type: 'line',
        data: revenueData,
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'top',
                },
                title: {
                    display: false,
                    text: 'Biểu đồ doanh thu theo tháng'
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function (value) {
                            return value.toLocaleString() + ' VNĐ';
                        }
                    }
                }
            }
        }
    });
}

function createRoomTypeChart(ctx) {
    // Dữ liệu mẫu cho biểu đồ loại phòng
    const roomTypeData = {
        labels: ['Phòng Deluxe', 'Phòng Suite', 'Phòng Standard', 'Phòng VIP', 'Phòng Family'],
        datasets: [{
            label: 'Số lượt đặt',
            data: [45, 32, 28, 18, 12],
            backgroundColor: [
                'rgba(22, 163, 74, 0.7)',
                'rgba(34, 197, 94, 0.7)',
                'rgba(74, 222, 128, 0.7)',
                'rgba(134, 239, 172, 0.7)',
                'rgba(167, 243, 208, 0.7)'
            ],
            borderColor: [
                'rgba(22, 163, 74, 1)',
                'rgba(34, 197, 94, 1)',
                'rgba(74, 222, 128, 1)',
                'rgba(134, 239, 172, 1)',
                'rgba(167, 243, 208, 1)'
            ],
            borderWidth: 1
        }]
    };
    
    new Chart(ctx, {
        type: 'doughnut',
        data: roomTypeData,
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'top',
                },
                title: {
                    display: false,
                    text: 'Biểu đồ hạng phòng được đặt nhiều nhất'
                }
            }
        }
    });
}
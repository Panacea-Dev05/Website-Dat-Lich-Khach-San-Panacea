const revenueCtx = document.getElementById('revenueChart').getContext('2d');
const occupancyCtx = document.getElementById('occupancyChart').getContext('2d');
const customerCtx = document.getElementById('customerChart').getContext('2d');
const promoEffectCtx = document.getElementById('promotionEffectivenessChart').getContext('2d');
const ratingSummaryCtx = document.getElementById('ratingSummaryChart').getContext('2d');

// Existing charts data
const revenueData = {
    labels: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6', 'Tháng 7'],
    datasets: [{
        label: 'Doanh thu (triệu VNĐ)',
        data: [400, 450, 420, 470, 480, 520, 500],
        fill: true,
        backgroundColor: 'rgba(16, 185, 129, 0.2)',
        borderColor: '#10b981',
        tension: 0.4,
        pointBackgroundColor: '#10b981',
        pointRadius: 6,
    }]
};
new Chart(revenueCtx, {
    type: 'line',
    data: revenueData,
    options: {
        responsive: true,
        plugins: { legend: { display: true }, tooltip: { mode: 'index', intersect: false } },
        scales: { y: { beginAtZero: true } }
    }
});

const occupancyData = {
    labels: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6', 'Tháng 7'],
    datasets: [{
        label: 'Occupancy Rate (%)',
        data: [75, 80, 78, 88, 85, 90, 87],
        backgroundColor: '#10b981',
        borderRadius: 8
    }]
};
new Chart(occupancyCtx, {
    type: 'bar',
    data: occupancyData,
    options: {
        responsive: true,
        plugins: { legend: { display: false }, tooltip: { mode: 'index', intersect: false } },
        scales: { y: { min: 0, max: 100, ticks: { stepSize: 10 }, title: { display: true, text: '%' } } }
    }
});

const customerData = {
    labels: ['Khách mới', 'Khách quay lại'],
    datasets: [{
        label: 'Tỷ lệ khách hàng',
        data: [60, 40],
        backgroundColor: ['#10b981', '#059669'],
        hoverOffset: 30,
    }]
};
new Chart(customerCtx, {
    type: 'doughnut',
    data: customerData,
    options: {
        responsive: true,
        plugins: { legend: { position: 'bottom' }, tooltip: { enabled: true } }
    }
});

// New chart: Hiệu quả chương trình khuyến mãi (bar chart)
const promoEffectData = {
    labels: ['KM001', 'KM002', 'KM003', 'KM004'],
    datasets: [
        {
            type: 'bar',
            label: 'Số lần sử dụng',
            data: [120, 80, 55, 40],
            backgroundColor: '#10b981',
            yAxisID: 'y'
        },
        {
            type: 'line',
            label: 'Doanh thu tạo ra (triệu VNĐ)',
            data: [480, 320, 210, 155],
            borderColor: '#059669',
            backgroundColor: '#059669',
            fill: false,
            yAxisID: 'y1',
            tension: 0.4,
            pointRadius: 6,
        }
    ]
};
new Chart(promoEffectCtx, {
    data: promoEffectData,
    options: {
        responsive: true,
        interaction: { mode: 'index', intersect: false },
        stacked: false,
        plugins: { legend: { position: 'top' } },
        scales: {
            y: { type: 'linear', position: 'left', title: { display: true, text: 'Số lần sử dụng' } },
            y1: { type: 'linear', position: 'right', title: { display: true, text: 'Doanh thu (triệu VNĐ)' }, grid: { drawOnChartArea: false } }
        }
    }
});


// New chart: Điểm đánh giá trung bình theo tiêu chí (radar chart)
const ratingSummaryData = {
    labels: ['Sạch sẽ', 'Tiện nghi', 'Dịch vụ', 'Vị trí', 'Giá cả'],
    datasets: [{
        label: 'Điểm trung bình',
        data: [4.5, 4.2, 4.8, 4.6, 4.0],
        backgroundColor: 'rgba(16, 185, 129, 0.4)',
        borderColor: '#10b981',
        borderWidth: 2,
        pointBackgroundColor: '#10b981',
        pointRadius: 6,
        fill: true
    }]
};
new Chart(ratingSummaryCtx, {
    type: 'radar',
    data: ratingSummaryData,
    options: {
        responsive: true,
        scales: {
            r: {
                beginAtZero: true,
                min: 0,
                max: 5,
                ticks: { stepSize: 1 },
                pointLabels: { font: { size: 14, weight: '600' }, color: '#064e3b' },
                grid: { color: '#d1fae5' },
                angleLines: { color: '#a7f3d0' },
            }
        },
        plugins: {
            legend: { position: 'top' },
            tooltip: { enabled: true }
        }
    }
});

// Sidebar toggle for mobile
const mobileToggle = document.getElementById('mobileToggle');
const sidebar = document.getElementById('sidebar');
mobileToggle.addEventListener('click', () => {
    sidebar.classList.toggle('open');
});

// Sidebar nav highlight (demo)
const navLinks = sidebar.querySelectorAll('nav ul li a');
const mainHeading = document.querySelector('main > h1');
navLinks.forEach(link=>{
    link.addEventListener('click', e => {
        e.preventDefault();
        navLinks.forEach(l => l.classList.remove('active'));
        link.classList.add('active');
        sidebar.classList.remove('open'); // close sidebar mobile

        mainHeading.textContent = link.textContent.trim();
        // For demo: no content switch
    });
});
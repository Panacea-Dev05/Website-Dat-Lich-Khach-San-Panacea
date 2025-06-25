// Sidebar toggle for mobile
const mobileToggle = document.getElementById('mobileToggle');
const sidebar = document.getElementById('sidebar');
mobileToggle.addEventListener('click', () => {
    sidebar.classList.toggle('open');
});

// Sidebar nav highlight (demo)
const navLinks = sidebar.querySelectorAll('nav ul li a');
const mainHeading = document.querySelector('main > h1');
navLinks.forEach(link => {
    link.addEventListener('click', e => {
        e.preventDefault();
        navLinks.forEach(l => l.classList.remove('active'));
        link.classList.add('active');
        sidebar.classList.remove('open'); // close sidebar mobile

        mainHeading.textContent = link.textContent.trim();
        // For demo: no content switch
    });
});

const settingsForm = document.getElementById('settingsForm');
const btnReset = document.getElementById('btnReset');

function saveSettings(data) {
    // Simulate saving settings (would connect to backend API)
    alert('Cài đặt đã được lưu thành công.');
    localStorage.setItem('panacea-settings', JSON.stringify(data));
}

function loadSettings() {
    const saved = localStorage.getItem('panacea-settings');
    if (!saved) return null;
    return JSON.parse(saved);
}

function setFormValues(data) {
    if (!data) return;
    settingsForm.hotelName.value = data.hotelName || '';
    settingsForm.hotelAddress.value = data.hotelAddress || '';
    settingsForm.hotelStars.value = data.hotelStars || '4';
    settingsForm.timezone.value = data.timezone || 'Asia/Ho_Chi_Minh';
    settingsForm.passwordPolicy.value = data.passwordPolicy || 'basic';
    settingsForm.passwordExpiry.value = data.passwordExpiry || 90;
    settingsForm.rateLimiting.value = data.rateLimiting || 10;
    document.getElementById('emailNotifications').checked = data.emailNotifications ?? true;
    document.getElementById('smsNotifications').checked = data.smsNotifications ?? false;
    document.getElementById('pushNotifications').checked = data.pushNotifications ?? true;
}

function getFormValues() {
    return {
        hotelName: settingsForm.hotelName.value.trim(),
        hotelAddress: settingsForm.hotelAddress.value.trim(),
        hotelStars: settingsForm.hotelStars.value,
        timezone: settingsForm.timezone.value,
        passwordPolicy: settingsForm.passwordPolicy.value,
        passwordExpiry: parseInt(settingsForm.passwordExpiry.value),
        rateLimiting: parseInt(settingsForm.rateLimiting.value),
        emailNotifications: document.getElementById('emailNotifications').checked,
        smsNotifications: document.getElementById('smsNotifications').checked,
        pushNotifications: document.getElementById('pushNotifications').checked,
    };
}

settingsForm.addEventListener('submit', e => {
    e.preventDefault();
    const data = getFormValues();

    // Basic validation examples
    if (!data.hotelName) return alert('Vui lòng nhập tên khách sạn.');
    if (!data.hotelAddress) return alert('Vui lòng nhập địa chỉ khách sạn.');
    if (data.passwordExpiry < 0 || data.passwordExpiry > 180) return alert('Thời gian hết hạn mật khẩu phải từ 0 đến 180 ngày.');
    if (data.rateLimiting < 1 || data.rateLimiting > 60) return alert('Giới hạn số request/IP phải từ 1 đến 60 phút.');

    saveSettings(data);
});

btnReset.addEventListener('click', () => {
    if (confirm('Bạn có chắc chắn muốn đặt lại tất cả cài đặt về mặc định?')) {
        localStorage.removeItem('panacea-settings');
        location.reload();
    }
});

// On page load
const settingsData = loadSettings();
if (settingsData) {
    setFormValues(settingsData);
}
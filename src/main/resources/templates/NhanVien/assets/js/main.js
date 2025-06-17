// Preloader
window.addEventListener('load', function() {
    const preloader = document.getElementById('preloader');
    preloader.style.display = 'none';
});

// Sticky Header
window.addEventListener('scroll', function() {
    const header = document.querySelector('.header');
    header.classList.toggle('sticky', window.scrollY > 0);
});

// Mobile Menu Toggle
document.querySelector('.navbar-toggler').addEventListener('click', function() {
    document.querySelector('.navbar-collapse').classList.toggle('show');
});

// Smooth Scroll
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        document.querySelector(this.getAttribute('href')).scrollIntoView({
            behavior: 'smooth'
        });
    });
});

// Form Validation
const bookingForm = document.querySelector('.booking-form form');
if (bookingForm) {
    bookingForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Get form values
        const checkIn = this.querySelector('input[type="date"]:first-of-type').value;
        const checkOut = this.querySelector('input[type="date"]:last-of-type').value;
        const guests = this.querySelector('select').value;
        
        // Validate dates
        if (!checkIn || !checkOut) {
            alert('Vui lòng chọn ngày nhận và trả phòng');
            return;
        }
        
        // Validate check-out date is after check-in date
        if (new Date(checkOut) <= new Date(checkIn)) {
            alert('Ngày trả phòng phải sau ngày nhận phòng');
            return;
        }
        
        // If validation passes, you can submit the form
        // For now, we'll just show a success message
        alert('Đặt phòng thành công! Chúng tôi sẽ liên hệ với bạn sớm nhất.');
        this.reset();
    });
}

// Animation on Scroll
const animateOnScroll = function() {
    const elements = document.querySelectorAll('.feature-box');
    
    elements.forEach(element => {
        const elementPosition = element.getBoundingClientRect().top;
        const screenPosition = window.innerHeight;
        
        if (elementPosition < screenPosition) {
            element.classList.add('animate');
        }
    });
};

window.addEventListener('scroll', animateOnScroll);

// Initialize date inputs with min date as today
const dateInputs = document.querySelectorAll('input[type="date"]');
const today = new Date().toISOString().split('T')[0];

dateInputs.forEach(input => {
    input.min = today;
});

// Add hover effect to feature boxes
const featureBoxes = document.querySelectorAll('.feature-box');
featureBoxes.forEach(box => {
    box.addEventListener('mouseenter', function() {
        this.style.transform = 'translateY(-10px)';
    });
    
    box.addEventListener('mouseleave', function() {
        this.style.transform = 'translateY(0)';
    });
}); 
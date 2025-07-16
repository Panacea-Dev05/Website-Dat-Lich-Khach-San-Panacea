document.addEventListener("DOMContentLoaded", () => {
  // Sidebar toggle for mobile
  const mobileToggle = document.getElementById("mobileToggle");
  const sidebar = document.getElementById("sidebar");

  if (mobileToggle && sidebar) {
    mobileToggle.addEventListener("click", () => {
      sidebar.classList.toggle("open");
    });
  }

  // --- Chart Initialization ---

  // 1. Revenue Chart
  const revenueCtx = document.getElementById("revenueChart");
  if (revenueCtx && typeof revenueData !== "undefined") {
    new Chart(revenueCtx, {
      type: "line",
      data: {
        labels: revenueData.labels || [],
        datasets: [
          {
            label: "Doanh thu",
            data: revenueData.data || [],
            borderColor: "#10b981",
            backgroundColor: "rgba(16, 185, 129, 0.1)",
            fill: true,
            tension: 0.4,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              callback: function (value) {
                return value / 1000000 + "tr";
              },
            },
          },
        },
      },
    });
  }

  // 2. Occupancy Chart
  const occupancyCtx = document.getElementById("occupancyChart");
  if (occupancyCtx && typeof occupancyData !== "undefined") {
    new Chart(occupancyCtx, {
      type: "bar",
      data: {
        labels: occupancyData.labels || [],
        datasets: [
          {
            label: "Công suất phòng (%)",
            data: occupancyData.data || [],
            backgroundColor: "#3b82f6",
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            beginAtZero: true,
            max: 100,
          },
        },
      },
    });
  }

  // 3. Customer Type Chart (Dữ liệu mẫu)
  const customerCtx = document.getElementById("customerChart");
  if (customerCtx) {
    new Chart(customerCtx, {
      type: "pie",
      data: {
        labels: ["Khách mới", "Khách cũ", "Khách VIP"],
        datasets: [
          {
            label: "Loại khách hàng",
            data: [120, 80, 30],
            backgroundColor: ["#ef4444", "#f97316", "#84cc16"],
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
      },
    });
  }

  // 4. Promotion Effectiveness Chart (Dữ liệu mẫu)
  const promotionCtx = document.getElementById("promotionEffectivenessChart");
  if (promotionCtx) {
    new Chart(promotionCtx, {
      type: "doughnut",
      data: {
        labels: ["KM Hè", "KM Cuối tuần", "Không KM"],
        datasets: [
          {
            label: "Lượt đặt phòng",
            data: [300, 150, 550],
            backgroundColor: ["#6366f1", "#a855f7", "#d946ef"],
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
      },
    });
  }

  // 5. Rating Summary Chart (Dữ liệu mẫu)
  const ratingCtx = document.getElementById("ratingSummaryChart");
  if (ratingCtx) {
    new Chart(ratingCtx, {
      type: "radar",
      data: {
        labels: ["Sạch sẽ", "Thoải mái", "Dịch vụ", "Tiện nghi", "Vị trí"],
        datasets: [
          {
            label: "Điểm trung bình",
            data: [4.5, 4.2, 4.8, 4.6, 4.9],
            backgroundColor: "rgba(234, 179, 8, 0.2)",
            borderColor: "#eab308",
            pointBackgroundColor: "#eab308",
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          r: {
            beginAtZero: true,
            max: 5,
          },
        },
      },
    });
  }
});

// JavaScript cho trang báo cáo thống kê
document.addEventListener("DOMContentLoaded", function () {
  console.log("Trang báo cáo thống kê đã được tải");

  // Khởi tạo các biểu đồ
  initializeCharts();

  // Auto refresh dữ liệu mỗi 5 phút
  setInterval(function () {
    location.reload();
  }, 300000);
});

function initializeCharts() {
  // Kiểm tra xem Chart.js đã được tải chưa
  if (typeof Chart === "undefined") {
    console.error("Chart.js chưa được tải");
    return;
  }

  console.log("Đang khởi tạo biểu đồ...");

  // Biểu đồ doanh thu
  const revenueCtx = document.getElementById("revenueChart");
  if (revenueCtx) {
    createRevenueChart(revenueCtx);
  }

  // Biểu đồ loại phòng
  const roomTypeCtx = document.getElementById("roomTypeChart");
  if (roomTypeCtx) {
    createRoomTypeChart(roomTypeCtx);
  }
}

function createRevenueChart(ctx) {
  // Lấy dữ liệu từ server
  const revenueData = getRevenueDataFromServer();

  const chartData = {
    labels: [
      "Tháng 1",
      "Tháng 2",
      "Tháng 3",
      "Tháng 4",
      "Tháng 5",
      "Tháng 6",
      "Tháng 7",
      "Tháng 8",
      "Tháng 9",
      "Tháng 10",
      "Tháng 11",
      "Tháng 12",
    ],
    datasets: [
      {
        label: "Doanh thu (VNĐ)",
        data: revenueData,
        backgroundColor: "rgba(16, 185, 129, 0.2)",
        borderColor: "rgba(16, 185, 129, 1)",
        borderWidth: 2,
        pointBackgroundColor: "rgba(16, 185, 129, 1)",
        pointBorderColor: "#fff",
        pointHoverRadius: 6,
        pointHoverBackgroundColor: "#fff",
        pointHoverBorderColor: "rgba(16, 185, 129, 1)",
        tension: 0.4,
        fill: true,
      },
    ],
  };

  new Chart(ctx, {
    type: "line",
    data: chartData,
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: "top",
        },
        title: {
          display: false,
          text: "Biểu đồ doanh thu theo tháng",
        },
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            callback: function (value) {
              return value.toLocaleString() + " VNĐ";
            },
          },
        },
      },
    },
  });
}

function createRoomTypeChart(ctx) {
  // Lấy dữ liệu từ server
  const roomTypeData = getRoomTypeDataFromServer();

  const chartData = {
    labels: roomTypeData.labels,
    datasets: [
      {
        label: "Số lượt đặt",
        data: roomTypeData.data,
        backgroundColor: [
          "rgba(22, 163, 74, 0.7)",
          "rgba(34, 197, 94, 0.7)",
          "rgba(74, 222, 128, 0.7)",
          "rgba(134, 239, 172, 0.7)",
          "rgba(167, 243, 208, 0.7)",
        ],
        borderColor: [
          "rgba(22, 163, 74, 1)",
          "rgba(34, 197, 94, 1)",
          "rgba(74, 222, 128, 1)",
          "rgba(134, 239, 172, 1)",
          "rgba(167, 243, 208, 1)",
        ],
        borderWidth: 1,
      },
    ],
  };

  new Chart(ctx, {
    type: "doughnut",
    data: chartData,
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: "top",
        },
        title: {
          display: false,
          text: "Biểu đồ hạng phòng được đặt nhiều nhất",
        },
      },
    },
  });
}

// Hàm lấy dữ liệu doanh thu từ server
function getRevenueDataFromServer() {
  try {
    // Lấy dữ liệu từ Thymeleaf template
    const monthlyRevenue = window.monthlyRevenue || {};
    const months = [
      "Tháng 1",
      "Tháng 2",
      "Tháng 3",
      "Tháng 4",
      "Tháng 5",
      "Tháng 6",
      "Tháng 7",
      "Tháng 8",
      "Tháng 9",
      "Tháng 10",
      "Tháng 11",
      "Tháng 12",
    ];

    return months.map((month) => {
      const value = monthlyRevenue[month];
      return value ? parseFloat(value) : 0;
    });
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu doanh thu:", error);
    return [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
  }
}

// Hàm lấy dữ liệu loại phòng từ server
function getRoomTypeDataFromServer() {
  try {
    // Lấy dữ liệu từ Thymeleaf template
    const roomTypeBookings = window.roomTypeBookings || {};

    const labels = Object.keys(roomTypeBookings);
    const data = Object.values(roomTypeBookings).map(
      (value) => parseInt(value) || 0
    );

    return { labels, data };
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu loại phòng:", error);
    return {
      labels: [
        "Phòng Deluxe",
        "Phòng Suite",
        "Phòng Standard",
        "Phòng VIP",
        "Phòng Family",
      ],
      data: [0, 0, 0, 0, 0],
    };
  }
}

/**
 * Thanh toán - Nhân viên
 * Xử lý các chức năng thanh toán và in hóa đơn cho khách hàng
 */

// JavaScript cho chức năng thanh toán nhân viên

$(document).ready(function () {
  // Khởi tạo các event handlers
  initializeEventHandlers();

  // Load dữ liệu ban đầu
  loadInitialData();
});

function initializeEventHandlers() {
  // Xử lý submit form tạo thanh toán
  $("#paymentForm").on("submit", function (e) {
    e.preventDefault();
    createPayment();
  });

  // Xử lý click nút xác nhận thanh toán
  $(document).on("click", ".btn-confirm-payment", function () {
    const paymentId = $(this).data("payment-id");
    confirmPayment(paymentId);
  });

  // Xử lý click nút in hóa đơn
  $(document).on("click", ".btn-print-invoice", function () {
    const paymentId = $(this).data("payment-id");
    printInvoice(paymentId);
  });

  // Xử lý click nút xem chi tiết
  $(document).on("click", ".btn-view-detail", function () {
    const paymentId = $(this).data("payment-id");
    viewPaymentDetail(paymentId);
  });

  // Validation real-time cho form
  $("#bookingId").on("change", function () {
    const value = $(this).val();
    if (value && value.trim() !== "") {
      validateBookingId(value);
    }
  });

  $("#soTien").on("input", function () {
    const value = $(this).val();
    if (value && value.trim() !== "") {
      validateAmount(value);
    }
  });

  // Validate payment method
  $("#phuongThuc").on("change", function () {
    const value = $(this).val();
    if (value && value.trim() !== "") {
      showFieldValid("phuongThuc");
    } else {
      showFieldError("phuongThuc", "Vui lòng chọn phương thức thanh toán");
    }
  });

  // Validate transaction code
  $("#maGiaoDich").on("input", function () {
    const value = $(this).val();
    if (value && value.trim() !== "") {
      if (value.trim().length < 3) {
        showFieldError("maGiaoDich", "Mã giao dịch phải có ít nhất 3 ký tự");
      } else if (value.trim().length > 50) {
        showFieldError("maGiaoDich", "Mã giao dịch không được quá 50 ký tự");
      } else {
        showFieldValid("maGiaoDich");
      }
    }
  });

  // Validate content
  $("#noiDung").on("input", function () {
    const value = $(this).val();
    if (value && value.trim().length > 500) {
      showFieldError("noiDung", "Nội dung không được quá 500 ký tự");
    } else {
      // Remove error if exists
      $("#noiDung")
        .removeClass("is-invalid")
        .siblings(".invalid-feedback")
        .remove();
    }
  });
}

function loadInitialData() {
  // Không cần refresh ngay khi load trang vì dữ liệu đã được render từ server
  // refreshPaymentTable(); // Bỏ comment này để tránh vòng lặp reload
  console.log("Trang thanh toán đã được khởi tạo");
}

// Cập nhật thông tin chi tiết thanh toán khi chọn booking
function updatePaymentInfo() {
  const bookingSelect = document.getElementById("bookingId");
  const selectedOption = bookingSelect.options[bookingSelect.selectedIndex];
  const paymentDetails = document.getElementById("paymentDetails");

  if (selectedOption.value === "" || selectedOption.disabled) {
    paymentDetails.style.display = "none";
    return;
  }

  // Lấy dữ liệu từ data attributes
  const roomTotal =
    parseFloat(selectedOption.getAttribute("data-room-total")) || 0;
  const serviceTotal =
    parseFloat(selectedOption.getAttribute("data-service-total")) || 0;
  const deposit = parseFloat(selectedOption.getAttribute("data-deposit")) || 0;
  const paymentTotal =
    parseFloat(selectedOption.getAttribute("data-payment-total")) || 0;

  // Cập nhật hiển thị
  document.getElementById("roomTotal").textContent = formatCurrency(roomTotal);
  document.getElementById("serviceTotal").textContent =
    formatCurrency(serviceTotal);
  document.getElementById("depositAmount").textContent =
    formatCurrency(deposit);
  document.getElementById("totalPayment").textContent =
    formatCurrency(paymentTotal);

  // Tự động điền số tiền cần thanh toán và không cho phép thay đổi
  const soTienInput = document.getElementById("soTien");
  soTienInput.value = paymentTotal;
  soTienInput.setAttribute("readonly", true);
  soTienInput.style.backgroundColor = "#f3f4f6";
  soTienInput.style.cursor = "not-allowed";

  // Hiển thị phần chi tiết
  paymentDetails.style.display = "block";
}

// Format số tiền thành định dạng VND
function formatCurrency(amount) {
  if (isNaN(amount) || amount === null || amount === undefined) {
    return "0 VND";
  }
  return new Intl.NumberFormat("vi-VN").format(amount) + " VND";
}

// Tạo thanh toán mới
function createPayment() {
  const formData = {
    bookingId: $("#bookingId").val()?.trim(),
    soTien: $("#soTien").val()?.trim(),
    phuongThuc: $("#phuongThuc").val()?.trim(),
    noiDung: $("#noiDung").val()?.trim(),
    maGiaoDich: $("#maGiaoDich").val()?.trim(),
  };

  // Validate dữ liệu
  if (!validatePaymentForm(formData)) {
    return;
  }

  // Kiểm tra trùng lặp request
  if (window.isCreatingPayment) {
    showError("Đang xử lý thanh toán, vui lòng đợi...");
    return;
  }

  window.isCreatingPayment = true;

  // Hiển thị loading
  showLoading("Đang tạo thanh toán...");

  $.ajax({
    url: "/nhanvien/thanhtoan/create",
    type: "POST",
    contentType: "application/json",
    data: JSON.stringify(formData),
    timeout: 30000, // 30 seconds timeout
    success: function (response) {
      hideLoading();
      window.isCreatingPayment = false;

      if (response && response.success) {
        showSuccess("Tạo thanh toán thành công!");
        resetForm();
        refreshPaymentTable();
      } else {
        const errorMsg =
          response?.message || "Có lỗi xảy ra khi tạo thanh toán";
        showError(errorMsg);
      }
    },
    error: function (xhr, textStatus, errorThrown) {
      hideLoading();
      window.isCreatingPayment = false;

      let errorMsg = "Lỗi kết nối server";

      if (textStatus === "timeout") {
        errorMsg = "Yêu cầu quá thời gian chờ, vui lòng thử lại";
      } else if (xhr.status === 0) {
        errorMsg = "Không thể kết nối đến server";
      } else if (xhr.status >= 400 && xhr.status < 500) {
        errorMsg = xhr.responseJSON?.message || "Dữ liệu không hợp lệ";
      } else if (xhr.status >= 500) {
        errorMsg = "Lỗi server nội bộ, vui lòng thử lại sau";
      } else if (xhr.responseJSON?.message) {
        errorMsg = xhr.responseJSON.message;
      }

      showError(errorMsg);
      console.error("Payment creation error:", {
        status: xhr.status,
        statusText: xhr.statusText,
        textStatus: textStatus,
        errorThrown: errorThrown,
        response: xhr.responseJSON,
      });
    },
  });
}

// Xác nhận thanh toán
function confirmPayment(paymentId) {
  // Validate payment ID
  if (!paymentId || !/^\d+$/.test(paymentId.toString())) {
    showError("ID thanh toán không hợp lệ");
    return;
  }

  if (!confirm("Bạn có chắc chắn muốn xác nhận thanh toán này?")) {
    return;
  }

  // Kiểm tra trùng lặp request
  if (window.isConfirmingPayment) {
    showError("Đang xử lý xác nhận, vui lòng đợi...");
    return;
  }

  window.isConfirmingPayment = true;
  showLoading("Đang xác nhận thanh toán...");

  $.ajax({
    url: `/nhanvien/thanhtoan/confirm/${paymentId}`,
    type: "PUT",
    timeout: 15000,
    success: function (response) {
      hideLoading();
      window.isConfirmingPayment = false;

      if (response && response.success) {
        showSuccess("Xác nhận thanh toán thành công!");
        refreshPaymentTable();
      } else {
        const errorMsg = response?.message || "Không thể xác nhận thanh toán";
        showError(errorMsg);
      }
    },
    error: function (xhr, textStatus, errorThrown) {
      hideLoading();
      window.isConfirmingPayment = false;

      let errorMsg = "Lỗi kết nối server";

      if (textStatus === "timeout") {
        errorMsg = "Yêu cầu quá thời gian chờ";
      } else if (xhr.status === 404) {
        errorMsg = "Không tìm thấy thanh toán";
      } else if (xhr.status === 400) {
        errorMsg = xhr.responseJSON?.message || "Thanh toán không thể xác nhận";
      } else if (xhr.responseJSON?.message) {
        errorMsg = xhr.responseJSON.message;
      }

      showError(errorMsg);
    },
  });
}

// In hóa đơn
function printInvoice(paymentId) {
  // Validate payment ID
  if (!paymentId || !/^\d+$/.test(paymentId.toString())) {
    showError("ID thanh toán không hợp lệ");
    return;
  }

  showLoading("Đang tạo hóa đơn...");

  $.ajax({
    url: `/nhanvien/thanhtoan/invoice/${paymentId}`,
    type: "GET",
    timeout: 15000,
    success: function (response) {
      hideLoading();

      if (response && response.success && response.invoice) {
        showInvoiceModal(response.invoice);
      } else {
        const errorMsg = response?.message || "Không thể tạo hóa đơn";
        showError(errorMsg);
      }
    },
    error: function (xhr, textStatus, errorThrown) {
      hideLoading();

      let errorMsg = "Lỗi kết nối server";

      if (textStatus === "timeout") {
        errorMsg = "Yêu cầu quá thời gian chờ";
      } else if (xhr.status === 404) {
        errorMsg = "Không tìm thấy thanh toán để tạo hóa đơn";
      } else if (xhr.status === 400) {
        errorMsg =
          xhr.responseJSON?.message ||
          "Không thể tạo hóa đơn cho thanh toán này";
      } else if (xhr.responseJSON?.message) {
        errorMsg = xhr.responseJSON.message;
      }

      showError(errorMsg);
    },
  });
}

// Xem chi tiết thanh toán
function viewPaymentDetail(paymentId) {
  // Validate payment ID
  if (!paymentId || !/^\d+$/.test(paymentId.toString())) {
    showError("ID thanh toán không hợp lệ");
    return;
  }

  // Kiểm tra nếu đang có request khác đang chạy
  if (window.paymentDetailLoading) {
    showError("Đang tải thông tin, vui lòng chờ...");
    return;
  }

  window.paymentDetailLoading = true;
  showLoading("Đang tải chi tiết thanh toán...");

  $.ajax({
    url: `/nhanvien/thanhtoan/detail/${paymentId}`,
    type: "GET",
    timeout: 10000,
    success: function (payment) {
      hideLoading();
      window.paymentDetailLoading = false;

      // Kiểm tra response có hợp lệ không
      if (payment && payment.id) {
        showPaymentDetailModal(payment);
      } else if (payment && payment.error) {
        showError(payment.message || "Lỗi khi tải thông tin thanh toán");
      } else {
        showError("Không thể tải thông tin thanh toán");
      }
    },
    error: function (xhr, textStatus, errorThrown) {
      hideLoading();
      window.paymentDetailLoading = false;

      let errorMsg = "Lỗi kết nối server";

      if (textStatus === "timeout") {
        errorMsg = "Yêu cầu quá thời gian chờ";
      } else if (xhr.status === 404) {
        errorMsg = "Không tìm thấy thông tin thanh toán";
      } else if (xhr.status === 500) {
        errorMsg = "Lỗi server, vui lòng thử lại sau";
      } else if (xhr.responseJSON?.message) {
        errorMsg = xhr.responseJSON.message;
      }

      console.error("Payment detail error:", {
        status: xhr.status,
        statusText: xhr.statusText,
        responseText: xhr.responseText,
        error: errorThrown,
      });

      showError(errorMsg);
    },
  });
}

// Validation functions
function validatePaymentForm(data) {
  // Reset validation states
  $(".form-control").removeClass("is-invalid is-valid");

  let isValid = true;
  let firstErrorField = null;

  // Validate booking ID
  if (!data.bookingId || data.bookingId.trim() === "") {
    showFieldError("bookingId", "Vui lòng chọn booking");
    isValid = false;
    if (!firstErrorField) firstErrorField = "#bookingId";
  } else if (!/^\d+$/.test(data.bookingId.trim())) {
    showFieldError("bookingId", "Booking ID không hợp lệ");
    isValid = false;
    if (!firstErrorField) firstErrorField = "#bookingId";
  } else {
    showFieldValid("bookingId");
  }

  // Validate amount
  if (!data.soTien || data.soTien.trim() === "") {
    showFieldError("soTien", "Vui lòng nhập số tiền");
    isValid = false;
    if (!firstErrorField) firstErrorField = "#soTien";
  } else {
    const amount = parseFloat(data.soTien.replace(/[^\d.-]/g, ""));
    if (isNaN(amount)) {
      showFieldError("soTien", "Số tiền phải là số hợp lệ");
      isValid = false;
      if (!firstErrorField) firstErrorField = "#soTien";
    } else if (amount <= 0) {
      showFieldError("soTien", "Số tiền phải lớn hơn 0");
      isValid = false;
      if (!firstErrorField) firstErrorField = "#soTien";
    } else if (amount > 999999999) {
      showFieldError("soTien", "Số tiền quá lớn (tối đa 999,999,999 VND)");
      isValid = false;
      if (!firstErrorField) firstErrorField = "#soTien";
    } else {
      showFieldValid("soTien");
    }
  }

  // Validate payment method
  if (!data.phuongThuc || data.phuongThuc.trim() === "") {
    showFieldError("phuongThuc", "Vui lòng chọn phương thức thanh toán");
    isValid = false;
    if (!firstErrorField) firstErrorField = "#phuongThuc";
  } else {
    const validMethods = ["CASH", "CHUYEN_KHOAN"];
    if (!validMethods.includes(data.phuongThuc)) {
      showFieldError("phuongThuc", "Phương thức thanh toán không hợp lệ");
      isValid = false;
      if (!firstErrorField) firstErrorField = "#phuongThuc";
    } else {
      showFieldValid("phuongThuc");
    }
  }

  // Validate transaction code if provided
  if (data.maGiaoDich && data.maGiaoDich.trim() !== "") {
    if (data.maGiaoDich.trim().length < 3) {
      showFieldError("maGiaoDich", "Mã giao dịch phải có ít nhất 3 ký tự");
      isValid = false;
      if (!firstErrorField) firstErrorField = "#maGiaoDich";
    } else if (data.maGiaoDich.trim().length > 50) {
      showFieldError("maGiaoDich", "Mã giao dịch không được quá 50 ký tự");
      isValid = false;
      if (!firstErrorField) firstErrorField = "#maGiaoDich";
    } else {
      showFieldValid("maGiaoDich");
    }
  }

  // Validate content if provided
  if (data.noiDung && data.noiDung.trim().length > 500) {
    showFieldError("noiDung", "Nội dung không được quá 500 ký tự");
    isValid = false;
    if (!firstErrorField) firstErrorField = "#noiDung";
  }

  // Focus on first error field
  if (!isValid && firstErrorField) {
    setTimeout(() => {
      $(firstErrorField).focus();
    }, 100);
  }

  return isValid;
}

function validateBookingId(bookingId) {
  if (!bookingId || bookingId.trim() === "") {
    showFieldError("bookingId", "Vui lòng chọn booking");
    return false;
  }

  if (!/^\d+$/.test(bookingId.trim())) {
    showFieldError("bookingId", "Booking ID không hợp lệ");
    return false;
  }

  // AJAX validation để kiểm tra booking có tồn tại không
  $.ajax({
    url: `/nhanvien/thanhtoan/booking/${bookingId}/status`,
    type: "GET",
    success: function (response) {
      if (response.success && response.data) {
        showFieldValid("bookingId");
        updatePaymentInfo();
      } else {
        showFieldError("bookingId", "Booking không tồn tại hoặc không hợp lệ");
      }
    },
    error: function (xhr) {
      if (xhr.status === 404) {
        showFieldError("bookingId", "Booking không tồn tại");
      } else {
        showFieldError("bookingId", "Không thể kiểm tra booking");
      }
    },
  });

  return true;
}

function validateAmount(amount) {
  if (!amount || amount.trim() === "") {
    showFieldError("soTien", "Vui lòng nhập số tiền");
    return false;
  }

  const numAmount = parseFloat(amount.replace(/[^\d.-]/g, ""));

  if (isNaN(numAmount)) {
    showFieldError("soTien", "Số tiền phải là số hợp lệ");
    return false;
  }

  if (numAmount <= 0) {
    showFieldError("soTien", "Số tiền phải lớn hơn 0");
    return false;
  }

  if (numAmount > 999999999) {
    showFieldError("soTien", "Số tiền quá lớn (tối đa 999,999,999 VND)");
    return false;
  }

  showFieldValid("soTien");
  return true;
}

// UI Helper functions
function showLoading(message) {
  // Clear any existing modals first
  forceClearAllModals();

  // Hiển thị loading spinner với message
  const loadingHtml = `
    <div class="modal fade" id="loadingModal" tabindex="-1" data-backdrop="static" data-keyboard="false">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-body text-center">
            <div class="spinner-border text-primary" role="status"></div>
            <p class="mt-2 mb-0" id="loadingMessage">${message}</p>
          </div>
        </div>
      </div>
    </div>
  `;

  $("body").append(loadingHtml);
  $("#loadingModal").modal("show");

  // Auto-clear loading after 30 seconds to prevent stuck state
  setTimeout(() => {
    if ($("#loadingModal").length) {
      console.warn("Loading modal auto-cleared after timeout");
      forceClearAllModals();
    }
  }, 30000);
}

function hideLoading() {
  try {
    // Force hide all loading modals
    $(".modal").modal("hide");

    // Remove all loading modals immediately
    $("#loadingModal").remove();

    // Clear any backdrop
    $(".modal-backdrop").remove();

    // Reset body class
    $("body").removeClass("modal-open");
    $("body").css("padding-right", "");

    console.log("Loading modal hidden successfully");
  } catch (error) {
    console.error("Error hiding loading modal:", error);
    // Fallback: force remove all modals and backdrops
    $(".modal").remove();
    $(".modal-backdrop").remove();
    $("body").removeClass("modal-open");
    $("body").css("padding-right", "");
  }
}

function showSuccess(message) {
  toastr.success(message);
}

function showError(message) {
  toastr.error(message);
}

function showInfo(message) {
  toastr.info(message);
}

function resetForm() {
  try {
    $("#paymentForm")[0].reset();
    $("#paymentForm .form-control").removeClass("is-valid is-invalid");

    // Reset payment details display
    const paymentDetails = document.getElementById("paymentDetails");
    if (paymentDetails) {
      paymentDetails.style.display = "none";
    }

    // Re-enable amount field if it was disabled
    const soTienInput = document.getElementById("soTien");
    if (soTienInput) {
      soTienInput.removeAttribute("readonly");
      soTienInput.style.backgroundColor = "";
      soTienInput.style.cursor = "";
    }

    // Clear any validation messages
    $(".invalid-feedback").hide();
    $(".valid-feedback").hide();
  } catch (error) {
    console.error("Error resetting form:", error);
    showError("Có lỗi khi reset form");
  }
}

function refreshPaymentTable() {
  try {
    // Hiển thị loading ngắn
    showInfo("Đang cập nhật dữ liệu...");

    // Delay reload để user thấy thông báo
    setTimeout(() => {
      location.reload();
    }, 500);
  } catch (error) {
    console.error("Error refreshing table:", error);
    // Fallback to immediate reload
    location.reload();
  }
}

// Helper functions for field validation display
function showFieldError(fieldId, message) {
  const field = $(`#${fieldId}`);
  field.removeClass("is-valid").addClass("is-invalid");

  // Remove existing error message
  field.siblings(".invalid-feedback").remove();

  // Add new error message
  field.after(`<div class="invalid-feedback">${message}</div>`);
}

function showFieldValid(fieldId) {
  const field = $(`#${fieldId}`);
  field.removeClass("is-invalid").addClass("is-valid");

  // Remove error message
  field.siblings(".invalid-feedback").remove();
}

// Modal functions
function showInvoiceModal(invoiceContent) {
  // Validate invoice content
  if (!invoiceContent || typeof invoiceContent !== "string") {
    showError("Nội dung hóa đơn không hợp lệ");
    return;
  }

  // Escape HTML to prevent XSS
  const escapedContent = $("<div>").text(invoiceContent).html();

  const modalHtml = `
        <div class="modal fade" id="invoiceModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Hóa đơn thanh toán</h5>
                        <button type="button" class="close" data-dismiss="modal">
                            <span>&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <pre style="white-space: pre-wrap; font-family: monospace; max-height: 400px; overflow-y: auto;">${escapedContent}</pre>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" onclick="printInvoiceContent()">In hóa đơn</button>
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Đóng</button>
                    </div>
                </div>
            </div>
        </div>
    `;

  try {
    // Remove existing modal if any
    $("#invoiceModal").remove();

    // Add new modal
    $("body").append(modalHtml);

    // Thêm event listener cho việc đóng modal
    $("#invoiceModal").on("hidden.bs.modal", function () {
      // Đảm bảo loading được ẩn khi modal đóng
      hideLoading();
    });

    // Thêm event listener cho việc đóng modal bằng ESC hoặc click outside
    $("#invoiceModal").on("hide.bs.modal", function () {
      // Đảm bảo loading được ẩn ngay khi modal bắt đầu đóng
      hideLoading();
    });

    $("#invoiceModal").modal("show");
  } catch (error) {
    console.error("Error showing invoice modal:", error);
    showError("Không thể hiển thị hóa đơn");
    // Đảm bảo loading được ẩn khi có lỗi
    hideLoading();
  }
}

function showPaymentDetailModal(payment) {
  // Validate payment data
  if (!payment || !payment.id) {
    showError("Dữ liệu thanh toán không hợp lệ");
    hideLoading();
    window.paymentDetailLoading = false;
    return;
  }

  // Safely get values with fallbacks
  const safeGet = (obj, path, fallback = "N/A") => {
    try {
      return path.split(".").reduce((o, p) => o && o[p], obj) || fallback;
    } catch {
      return fallback;
    }
  };

  // Escape HTML to prevent XSS
  const escapeHtml = (text) => {
    if (!text) return "N/A";
    return $("<div>").text(text.toString()).html();
  };

  const modalHtml = `
        <div class="modal fade" id="paymentDetailModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Chi tiết thanh toán #${escapeHtml(
                          payment.id
                        )}</h5>
                        <button type="button" class="close" data-dismiss="modal">
                            <span>&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-sm-4"><strong>Mã booking:</strong></div>
                            <div class="col-sm-8">${escapeHtml(
                              safeGet(payment, "bookingId", "N/A")
                            )}</div>
                        </div>
                        <div class="row mt-2">
                            <div class="col-sm-4"><strong>Số tiền:</strong></div>
                            <div class="col-sm-8">${formatCurrencySafe(
                              safeGet(payment, "soTien")
                            )}</div>
                        </div>
                        <div class="row mt-2">
                            <div class="col-sm-4"><strong>Phương thức:</strong></div>
                            <div class="col-sm-8">${escapeHtml(
                              safeGet(payment, "phuongThuc", "N/A")
                            )}</div>
                        </div>
                        <div class="row mt-2">
                            <div class="col-sm-4"><strong>Trạng thái:</strong></div>
                            <div class="col-sm-8"><span class="badge badge-${getStatusBadgeClass(
                              safeGet(payment, "trangThai")
                            )}">${escapeHtml(
    safeGet(payment, "trangThai", "N/A")
  )}</span></div>
                        </div>
                        <div class="row mt-2">
                            <div class="col-sm-4"><strong>Khách hàng:</strong></div>
                            <div class="col-sm-8">${escapeHtml(
                              safeGet(payment, "customerName", "N/A")
                            )}</div>
                        </div>
                        <div class="row mt-2">
                            <div class="col-sm-4"><strong>Nội dung:</strong></div>
                            <div class="col-sm-8">${escapeHtml(
                              safeGet(payment, "noiDung", "Không có")
                            )}</div>
                        </div>
                        <div class="row mt-2">
                            <div class="col-sm-4"><strong>Mã giao dịch:</strong></div>
                            <div class="col-sm-8">${escapeHtml(
                              safeGet(payment, "maGiaoDich", "Không có")
                            )}</div>
                        </div>
                        <div class="row mt-2">
                            <div class="col-sm-4"><strong>Ngày tạo:</strong></div>
                            <div class="col-sm-8">${formatDateTimeSafe(
                              safeGet(payment, "ngayTao")
                            )}</div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Đóng</button>
                    </div>
                </div>
            </div>
        </div>
    `;

  try {
    // Remove existing modal if any
    $("#paymentDetailModal").remove();

    // Add new modal
    $("body").append(modalHtml);

    // Thêm event listener cho việc đóng modal
    $("#paymentDetailModal").on("hidden.bs.modal", function () {
      // Đảm bảo loading được ẩn khi modal đóng
      hideLoading();
      window.paymentDetailLoading = false;
    });

    // Thêm event listener cho việc đóng modal bằng ESC hoặc click outside
    $("#paymentDetailModal").on("hide.bs.modal", function () {
      // Đảm bảo loading được ẩn ngay khi modal bắt đầu đóng
      hideLoading();
      window.paymentDetailLoading = false;
    });

    $("#paymentDetailModal").modal("show");
  } catch (error) {
    console.error("Error showing payment detail modal:", error);
    showError("Không thể hiển thị chi tiết thanh toán");
    // Đảm bảo loading được ẩn khi có lỗi
    hideLoading();
    window.paymentDetailLoading = false;
  }
}

function printInvoiceContent() {
  try {
    const content = $("#invoiceModal pre").text();

    if (!content || content.trim() === "") {
      showError("Không có nội dung hóa đơn để in");
      return;
    }

    const printWindow = window.open("", "_blank");

    if (!printWindow) {
      showError("Không thể mở cửa sổ in. Vui lòng kiểm tra popup blocker.");
      return;
    }

    printWindow.document.write(`
            <html>
                <head>
                    <title>Hóa đơn thanh toán</title>
                    <style>
                        body { font-family: monospace; white-space: pre-wrap; margin: 20px; }
                        @media print { body { margin: 0; } }
                    </style>
                </head>
                <body>
                    ${$("<div>").text(content).html()}
                </body>
            </html>
        `);

    printWindow.document.close();

    // Wait for content to load before printing
    setTimeout(() => {
      printWindow.print();
    }, 500);
  } catch (error) {
    console.error("Error printing invoice:", error);
    showError("Có lỗi khi in hóa đơn");
  }
}

// Utility functions
function formatCurrency(amount) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount);
}

function formatCurrencySafe(amount) {
  try {
    if (amount === null || amount === undefined || isNaN(amount)) {
      return "0 VND";
    }
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(amount);
  } catch (error) {
    console.error("Error formatting currency:", error);
    return amount ? amount.toString() + " VND" : "0 VND";
  }
}

function formatDateTime(dateTime) {
  if (!dateTime) return "N/A";
  return new Date(dateTime).toLocaleString("vi-VN");
}

function formatDateTimeSafe(dateTime) {
  try {
    if (!dateTime) return "N/A";
    const date = new Date(dateTime);
    if (isNaN(date.getTime())) {
      return "Ngày không hợp lệ";
    }
    return date.toLocaleString("vi-VN");
  } catch (error) {
    console.error("Error formatting date:", error);
    return "N/A";
  }
}

function getStatusBadgeClass(status) {
  switch (status) {
    case "THANH_CONG":
      return "success";
    case "CHO_XAC_NHAN":
      return "warning";
    case "THAT_BAI":
      return "danger";
    case "HUY":
      return "secondary";
    default:
      return "info";
  }
}

// Function to force clear all modal states
function forceClearAllModals() {
  try {
    // Hide all modals
    $(".modal").modal("hide");

    // Remove all modals
    $(".modal").remove();

    // Remove all backdrops
    $(".modal-backdrop").remove();

    // Reset body
    $("body").removeClass("modal-open");
    $("body").css("padding-right", "");

    // Reset loading states
    window.paymentDetailLoading = false;
    window.isCreatingPayment = false;
    window.isConfirmingPayment = false;

    console.log("All modals cleared successfully");
  } catch (error) {
    console.error("Error clearing modals:", error);
  }
}

// Global error handler for unhandled errors
window.addEventListener("error", function (event) {
  console.error("Unhandled error:", event.error);
  forceClearAllModals();
  showError("Đã xảy ra lỗi không mong muốn. Vui lòng thử lại.");
});

// Global handler for unhandled promise rejections
window.addEventListener("unhandledrejection", function (event) {
  console.error("Unhandled promise rejection:", event.reason);
  forceClearAllModals();
  showError("Đã xảy ra lỗi không mong muốn. Vui lòng thử lại.");
  event.preventDefault();
});

// Emergency function to clear modals (can be called from console)
window.clearModals = forceClearAllModals;

// Add keyboard shortcut to clear modals (Ctrl+Shift+C)
document.addEventListener("keydown", function (event) {
  if (event.ctrlKey && event.shiftKey && event.key === "C") {
    forceClearAllModals();
    console.log("Modals cleared by keyboard shortcut");
  }
});

// Cấu hình toastr
if (typeof toastr !== "undefined") {
  toastr.options = {
    closeButton: true,
    debug: false,
    newestOnTop: true,
    progressBar: true,
    positionClass: "toast-top-right",
    preventDuplicates: true,
    onclick: null,
    showDuration: "300",
    hideDuration: "1000",
    timeOut: "5000",
    extendedTimeOut: "1000",
    showEasing: "swing",
    hideEasing: "linear",
    showMethod: "fadeIn",
    hideMethod: "fadeOut",
  };
} else {
  console.warn(
    "Toastr library not loaded. Notifications may not work properly."
  );
}

// Helper functions for field validation display
function showFieldError(fieldId, message) {
  const field = $(`#${fieldId}`);
  field.removeClass("is-valid").addClass("is-invalid");

  // Remove existing error message
  field.siblings(".invalid-feedback").remove();

  // Add new error message
  field.after(`<div class="invalid-feedback">${message}</div>`);
}

function showFieldValid(fieldId) {
  const field = $(`#${fieldId}`);
  field.removeClass("is-invalid").addClass("is-valid");

  // Remove error message
  field.siblings(".invalid-feedback").remove();
}

let reviews = [
    {id: 1, code: "DG001", customer: "Nguyễn Văn A", bookingCode: "BK001", rating: 5, content: "Phòng rất đẹp, nhân viên thân thiện.", status: "Published", reply: "Cảm ơn quý khách!"},
    {id: 2, code: "DG002", customer: "Trần Thị B", bookingCode: "BK002", rating: 3, content: "Phòng hơi nhỏ, bữa sáng ổn.", status: "Pending", reply: ""},
];
let editingReviewId = null;

const reviewTableBody = document.getElementById('reviewTableBody');
const reviewForm = document.getElementById('reviewForm');
const formTitle = document.getElementById('formTitle');
const submitBtn = document.getElementById('submitBtn');
const cancelBtn = document.getElementById('cancelBtn');
const btnAddReview = document.getElementById('btnAddReview');

const inputReviewCode = document.getElementById('reviewCode');
const inputCustomerName = document.getElementById('customerName');
const inputBookingCode = document.getElementById('bookingCode');
const selectRating = document.getElementById('rating');
const textareaContent = document.getElementById('content');
const selectStatus = document.getElementById('status');
const textareaReply = document.getElementById('reply');

function renderReviewTable() {
    reviewTableBody.innerHTML = '';
    reviews.forEach((rvw, index) => {
        const tr = document.createElement('tr');
        tr.tabIndex = 0;
        tr.innerHTML = `
          <td>${index + 1}</td>
          <td>${rvw.code}</td>
          <td>${rvw.customer}</td>
          <td>${rvw.bookingCode}</td>
          <td>${rvw.rating}</td>
          <td title="${rvw.content}">${rvw.content.length > 40 ? rvw.content.slice(0, 40) + "..." : rvw.content}</td>
          <td>${statusText(rvw.status)}</td>
          <td title="${rvw.reply}">${rvw.reply.length > 30 ? rvw.reply.slice(0, 30) + "..." : rvw.reply}</td>
          <td>
            <button class="btn btnEdit" data-id="${rvw.id}" style="padding: 6px 12px; font-size: 0.8rem;">Sửa</button>
            <button class="btn btnToggle" data-id="${rvw.id}" style="padding: 6px 12px; font-size: 0.8rem; background:#ef4444; box-shadow:none;">
              ${rvw.status==="Hidden" ? "Hiển thị":"Ẩn"}
            </button>
          </td>
        `;
        reviewTableBody.appendChild(tr);
    });
    attachTableButtonEvents();
}
function statusText(st){
    switch(st){
        case "Published": return "Hiển thị";
        case "Hidden": return "Ẩn";
        case "Pending": return "Chờ duyệt";
        default: return st;
    }
}
function clearForm() {
    reviewForm.reset();
    editingReviewId = null;
    formTitle.textContent = "Tạo đánh giá mới";
    submitBtn.textContent = "Lưu đánh giá";
}
function showForm() {
    reviewForm.style.display = 'grid';
    btnAddReview.style.display = 'none';
    window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});
    inputReviewCode.focus();
}
function hideForm() {
    reviewForm.style.display = 'none';
    btnAddReview.style.display = 'inline-block';
    clearForm();
}
function attachTableButtonEvents() {
    document.querySelectorAll('.btnEdit').forEach(btn => {
        btn.onclick = e => {
            const id = Number(e.target.dataset.id);
            editReview(id);
        };
    });
    document.querySelectorAll('.btnToggle').forEach(btn => {
        btn.onclick = e => {
            const id = Number(e.target.dataset.id);
            toggleReviewStatus(id);
        };
    });
}
function editReview(id) {
    const rvw = reviews.find(r => r.id === id);
    if(!rvw) return;
    editingReviewId = id;
    formTitle.textContent = "Cập nhật đánh giá";
    submitBtn.textContent = "Cập nhật";
    inputReviewCode.value = rvw.code;
    inputCustomerName.value = rvw.customer;
    inputBookingCode.value = rvw.bookingCode;
    selectRating.value = rvw.rating;
    textareaContent.value = rvw.content;
    selectStatus.value = rvw.status;
    textareaReply.value = rvw.reply;
    showForm();
}
function toggleReviewStatus(id){
    const rvw = reviews.find(r => r.id === id);
    if(!rvw) return;
    const action = rvw.status === "Hidden" ? "hiển thị" : "ẩn";
    if(confirm(`Bạn có chắc chắn muốn ${action} đánh giá ${rvw.code}?`)){
        rvw.status = rvw.status === "Hidden" ? "Published" : "Hidden";
        renderReviewTable();
    }
}
reviewForm.onsubmit = e => {
    e.preventDefault();
    const code = inputReviewCode.value.trim();
    const customer = inputCustomerName.value.trim();
    const bookingCode = inputBookingCode.value.trim();
    const rating = selectRating.value;
    const content = textareaContent.value.trim();
    const status = selectStatus.value;
    const reply = textareaReply.value.trim();
    if(!code || !customer || !bookingCode || !rating || !content || !status){
        alert("Vui lòng điền đầy đủ thông tin đánh giá.");
        return;
    }
    if(editingReviewId){
        const idx = reviews.findIndex(r => r.id === editingReviewId);
        if(idx === -1) return;
        reviews[idx] = {id: editingReviewId, code, customer, bookingCode, rating, content, status, reply};
        alert("Cập nhật đánh giá thành công.");
    } else {
        if(reviews.some(r => r.code === code)){
            alert("Mã đánh giá đã tồn tại.");
            return;
        }
        const newId = reviews.length ? Math.max(...reviews.map(r=>r.id)) +1 : 1;
        reviews.push({id: newId, code, customer, bookingCode, rating, content, status, reply});
        alert("Tạo đánh giá mới thành công.");
    }
    renderReviewTable();
    hideForm();
}
cancelBtn.onclick = () => {
    hideForm();
}
btnAddReview.onclick = () => {
    clearForm();
    showForm();
}
// Sidebar toggle for mobile
const mobileToggle = document.getElementById('mobileToggle');
const sidebar = document.getElementById('sidebar');
mobileToggle.addEventListener('click', () => {
    sidebar.classList.toggle('open');
});
// Sidebar nav active link highlight and content change placeholder (for demo)
const navLinks = sidebar.querySelectorAll('nav ul li a');
const mainHeading = document.querySelector('main > h1');
const contentSection = document.getElementById('contentSection');

navLinks.forEach(link => {
    link.addEventListener('click', e => {
        e.preventDefault();
        navLinks.forEach(l => l.classList.remove('active'));
        link.classList.add('active');
        sidebar.classList.remove('open'); // auto-close sidebar on mobile
        const section = link.dataset.section;

        if(section !== 'reviews'){
            mainHeading.textContent = link.textContent.trim();
            contentSection.innerHTML = '<p style="padding:1rem; color:#065f46; font-weight:600;">Chức năng ' + link.textContent.trim() + ' sẽ được phát triển tiếp theo.</p>';
        } else {
            mainHeading.textContent = 'Quản lý đánh giá';
            contentSection.innerHTML = `
            <button class="btn" id="btnAddReview">Tạo đánh giá mới</button>
            <table aria-label="Danh sách đánh giá" id="reviewTable">
              <thead>
                <tr>
                  <th>STT</th>
                  <th>Mã đánh giá</th>
                  <th>Khách hàng</th>
                  <th>Booking</th>
                  <th>Điểm</th>
                  <th>Nội dung</th>
                  <th>Trạng thái</th>
                  <th>Phản hồi</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody id="reviewTableBody"></tbody>
            </table>
            <form id="reviewForm" style="display:none; margin-top: 2rem;">
              <h2 id="formTitle" style="color:#064e3b; margin-bottom:1rem;">Tạo đánh giá mới</h2>
              <label for="reviewCode">Mã đánh giá</label>
              <input type="text" id="reviewCode" name="reviewCode" required placeholder="DG001" pattern="^DG[0-9]{3,}$" title="Mã đánh giá bắt đầu DG theo sau là số" />
              <label for="customerName">Khách hàng</label>
              <input type="text" id="customerName" name="customerName" required placeholder="Tên khách hàng" />
              <label for="bookingCode">Mã booking</label>
              <input type="text" id="bookingCode" name="bookingCode" required placeholder="BK001" pattern="^(BK)[0-9]{3,}$" title="Mã booking bắt đầu BK theo sau là số" />
              <label for="rating">Điểm (1-5)</label>
              <select id="rating" name="rating" required>
                <option value="" disabled selected>Chọn điểm</option>
                <option value="1">1 - Rất kém</option>
                <option value="2">2 - Kém</option>
                <option value="3">3 - Trung bình</option>
                <option value="4">4 - Tốt</option>
                <option value="5">5 - Xuất sắc</option>
              </select>
              <label for="content">Nội dung đánh giá</label>
              <textarea id="content" name="content" required placeholder="Nhập nội dung đánh giá"></textarea>
              <label for="status">Trạng thái</label>
              <select id="status" name="status" required>
                <option value="" disabled selected>Chọn trạng thái</option>
                <option value="Published">Hiển thị</option>
                <option value="Hidden">Ẩn</option>
                <option value="Pending">Chờ duyệt</option>
              </select>
              <label for="reply">Phản hồi khách sạn</label>
              <textarea id="reply" name="reply" placeholder="Phản hồi khách hàng (không bắt buộc)"></textarea>
              <div style="grid-column: 2 / 3; margin-top: 1.5rem;">
                <button type="submit" class="btn" id="submitBtn">Lưu đánh giá</button>
                <button type="button" class="btn" id="cancelBtn" style="background:#ef4444; margin-left: 12px;">Hủy</button>
              </div>
            </form>
          `;
            setTimeout(setupReviewFormJS);
        }
    });
});

// Setup JS form events after inject
function setupReviewFormJS() {
    reviews = reviews || [];
    editingReviewId = null;
    const reviewTableBodyDyn = document.getElementById('reviewTableBody');
    const reviewFormDyn = document.getElementById('reviewForm');
    const formTitleDyn = document.getElementById('formTitle');
    const submitBtnDyn = document.getElementById('submitBtn');
    const cancelBtnDyn = document.getElementById('cancelBtn');
    const btnAddReviewDyn = document.getElementById('btnAddReview');

    const inputReviewCodeDyn = document.getElementById('reviewCode');
    const inputCustomerNameDyn = document.getElementById('customerName');
    const inputBookingCodeDyn = document.getElementById('bookingCode');
    const selectRatingDyn = document.getElementById('rating');
    const textareaContentDyn = document.getElementById('content');
    const selectStatusDyn = document.getElementById('status');
    const textareaReplyDyn = document.getElementById('reply');

    function renderReviewTableDyn() {
        reviewTableBodyDyn.innerHTML = '';
        reviews.forEach((rvw, index) => {
            const tr = document.createElement('tr');
            tr.tabIndex = 0;
            tr.innerHTML = `
            <td>${index + 1}</td>
            <td>${rvw.code}</td>
            <td>${rvw.customer}</td>
            <td>${rvw.bookingCode}</td>
            <td>${rvw.rating}</td>
            <td title="${rvw.content}">${rvw.content.length > 40 ? rvw.content.slice(0, 40) + '...' : rvw.content}</td>
            <td>${statusTextDyn(rvw.status)}</td>
            <td title="${rvw.reply}">${rvw.reply.length > 30 ? rvw.reply.slice(0, 30) + '...' : rvw.reply}</td>
            <td>
              <button class="btn btnEdit" data-id="${rvw.id}" style="padding: 6px 12px; font-size: 0.8rem;">Sửa</button>
              <button class="btn btnToggle" data-id="${rvw.id}" style="padding: 6px 12px; font-size: 0.8rem; background:#ef4444; box-shadow:none;">
                ${rvw.status === "Hidden" ? "Hiển thị" : "Ẩn"}
              </button>
            </td>
          `;
            reviewTableBodyDyn.appendChild(tr);
        });
        attachTableButtonEventsDyn();
    }
    function statusTextDyn(st) {
        switch (st) {
            case "Published": return "Hiển thị";
            case "Hidden": return "Ẩn";
            case "Pending": return "Chờ duyệt";
            default: return st;
        }
    }
    function clearFormDyn() {
        reviewFormDyn.reset();
        editingReviewId = null;
        formTitleDyn.textContent = "Tạo đánh giá mới";
        submitBtnDyn.textContent = "Lưu đánh giá";
    }
    function showFormDyn() {
        reviewFormDyn.style.display = 'grid';
        btnAddReviewDyn.style.display = 'none';
        window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' });
        inputReviewCodeDyn.focus();
    }
    function hideFormDyn() {
        reviewFormDyn.style.display = 'none';
        btnAddReviewDyn.style.display = 'inline-block';
        clearFormDyn();
    }
    function attachTableButtonEventsDyn() {
        document.querySelectorAll('.btnEdit').forEach(btn => {
            btn.onclick = e => {
                const id = Number(e.target.dataset.id);
                editReviewDyn(id);
            };
        });
        document.querySelectorAll('.btnToggle').forEach(btn => {
            btn.onclick = e => {
                const id = Number(e.target.dataset.id);
                toggleReviewStatusDyn(id);
            };
        });
    }
    function editReviewDyn(id) {
        const rvw = reviews.find(r => r.id === id);
        if (!rvw) return;
        editingReviewId = id;
        formTitleDyn.textContent = "Cập nhật đánh giá";
        submitBtnDyn.textContent = "Cập nhật";
        inputReviewCodeDyn.value = rvw.code;
        inputCustomerNameDyn.value = rvw.customer;
        inputBookingCodeDyn.value = rvw.bookingCode;
        selectRatingDyn.value = rvw.rating;
        textareaContentDyn.value = rvw.content;
        selectStatusDyn.value = rvw.status;
        textareaReplyDyn.value = rvw.reply;
        showFormDyn();
    }
    function toggleReviewStatusDyn(id) {
        const rvw = reviews.find(r => r.id === id);
        if (!rvw) return;
        const action = rvw.status === "Hidden" ? "hiển thị" : "ẩn";
        if (confirm(`Bạn có chắc chắn muốn ${action} đánh giá ${rvw.code}?`)) {
            rvw.status = rvw.status === "Hidden" ? "Published" : "Hidden";
            renderReviewTableDyn();
        }
    }
    reviewFormDyn.onsubmit = e => {
        e.preventDefault();
        const code = inputReviewCodeDyn.value.trim();
        const customer = inputCustomerNameDyn.value.trim();
        const bookingCode = inputBookingCodeDyn.value.trim();
        const rating = selectRatingDyn.value;
        const content = textareaContentDyn.value.trim();
        const status = selectStatusDyn.value;
        const reply = textareaReplyDyn.value.trim();
        if (!code || !customer || !bookingCode || !rating || !content || !status) {
            alert("Vui lòng điền đầy đủ thông tin đánh giá.");
            return;
        }
        if (editingReviewId) {
            const idx = reviews.findIndex(r => r.id === editingReviewId);
            if (idx === -1) return;
            reviews[idx] = { id: editingReviewId, code, customer, bookingCode, rating, content, status, reply };
            alert("Cập nhật đánh giá thành công.");
        } else {
            if (reviews.some(r => r.code === code)) {
                alert("Mã đánh giá đã tồn tại.");
                return;
            }
            const newId = reviews.length ? Math.max(...reviews.map(r => r.id)) + 1 : 1;
            reviews.push({ id: newId, code, customer, bookingCode, rating, content, status, reply });
            alert("Tạo đánh giá mới thành công.");
        }
        renderReviewTableDyn();
        hideFormDyn();
    }
    cancelBtn.onclick = () => {
        hideFormDyn();
    }
    btnAddReview.onclick = () => {
        clearFormDyn();
        showFormDyn();
    }
    renderReviewTableDyn();
}
// Sidebar toggle for mobile
const mobileToggle = document.getElementById('mobileToggle');
const sidebar = document.getElementById('sidebar');
mobileToggle.addEventListener('click', () => {
    sidebar.classList.toggle('open');
});
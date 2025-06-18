let promotions = [
    {id: 1, code: "KM001", name: "Khuyến mãi hè", discountPercent: 20, startDate: "2024-07-01", endDate: "2024-07-31", status: "Active", description: "Giảm 20% cho tất cả phòng trong tháng 7."},
    {id: 2, code: "KM002", name: "Giảm giá cuối tuần", discountPercent: 15, startDate: "2024-07-01", endDate: "2024-07-31", status: "Disabled", description: "Ưu đãi đặc biệt ngày cuối tuần."},
];
let editingPromotionId = null;

const promotionTableBody = document.getElementById('promotionTableBody');
const promotionForm = document.getElementById('promotionForm');
const formTitle = document.getElementById('formTitle');
const submitBtn = document.getElementById('submitBtn');
const cancelBtn = document.getElementById('cancelBtn');
const btnAddPromotion = document.getElementById('btnAddPromotion');

const inputPromoCode = document.getElementById('promoCode');
const inputPromoName = document.getElementById('promoName');
const inputDiscountPercent = document.getElementById('discountPercent');
const inputStartDate = document.getElementById('startDate');
const inputEndDate = document.getElementById('endDate');
const selectStatus = document.getElementById('status');
const textareaDescription = document.getElementById('description');

function renderPromotionTable() {
    promotionTableBody.innerHTML = '';
    promotions.forEach((promo, index) => {
        const tr = document.createElement('tr');
        tr.tabIndex = 0;
        tr.innerHTML = `
          <td>${index + 1}</td>
          <td>${promo.code}</td>
          <td>${promo.name}</td>
          <td>${promo.discountPercent}%</td>
          <td>${promo.startDate}</td>
          <td>${promo.endDate}</td>
          <td>${promo.status === 'Active' ? 'Hoạt động' : 'Vô hiệu hóa'}</td>
          <td title="${promo.description}">${promo.description.length>30 ? promo.description.slice(0,30)+"..." : promo.description}</td>
          <td>
            <button class="btn btnEdit" data-id="${promo.id}" style="padding: 6px 12px; font-size: 0.8rem;">Sửa</button>
            <button class="btn btnDelete" data-id="${promo.id}" style="padding: 6px 12px; font-size: 0.8rem; background:#ef4444; box-shadow:none;">Xóa</button>
          </td>
        `;
        promotionTableBody.appendChild(tr);
    });
    attachTableButtonEvents();
}

function clearForm() {
    promotionForm.reset();
    editingPromotionId = null;
    formTitle.textContent = "Tạo chương trình khuyến mãi";
    submitBtn.textContent = "Lưu khuyến mãi";
}
function showForm() {
    promotionForm.style.display = 'grid';
    btnAddPromotion.style.display = 'none';
    window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});
    inputPromoCode.focus();
}
function hideForm() {
    promotionForm.style.display = 'none';
    btnAddPromotion.style.display = 'inline-block';
    clearForm();
}
function attachTableButtonEvents() {
    document.querySelectorAll('.btnEdit').forEach(btn => {
        btn.onclick = e => {
            const id = Number(e.target.dataset.id);
            editPromotion(id);
        };
    });
    document.querySelectorAll('.btnDelete').forEach(btn => {
        btn.onclick = e => {
            const id = Number(e.target.dataset.id);
            deletePromotion(id);
        };
    });
}
function editPromotion(id) {
    const promo = promotions.find(p => p.id === id);
    if (!promo) return;
    editingPromotionId = id;
    formTitle.textContent = "Cập nhật khuyến mãi";
    submitBtn.textContent = "Cập nhật";
    inputPromoCode.value = promo.code;
    inputPromoName.value = promo.name;
    inputDiscountPercent.value = promo.discountPercent;
    inputStartDate.value = promo.startDate;
    inputEndDate.value = promo.endDate;
    selectStatus.value = promo.status;
    textareaDescription.value = promo.description;
    showForm();
}
function deletePromotion(id) {
    if(confirm('Bạn có chắc chắn muốn xóa chương trình khuyến mãi này?')){
        promotions = promotions.filter(p => p.id !== id);
        renderPromotionTable();
    }
}
promotionForm.onsubmit = e => {
    e.preventDefault();
    const code = inputPromoCode.value.trim();
    const name = inputPromoName.value.trim();
    const discountPercent = Number(inputDiscountPercent.value);
    const startDate = inputStartDate.value;
    const endDate = inputEndDate.value;
    const statusVal = selectStatus.value;
    const description = textareaDescription.value.trim();
    if(!code || !name || discountPercent === 0 || !startDate || !endDate || !statusVal){
        alert('Vui lòng điền đầy đủ thông tin hợp lệ.');
        return;
    }
    if(new Date(endDate) < new Date(startDate)){
        alert('Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu.');
        return;
    }
    if(!editingPromotionId && promotions.some(p => p.code === code)){
        alert('Mã khuyến mãi đã tồn tại.');
        return;
    }
    if(editingPromotionId) {
        const idx = promotions.findIndex(p => p.id === editingPromotionId);
        if(idx === -1) return;
        promotions[idx] = {id: editingPromotionId, code, name, discountPercent, startDate, endDate, status: statusVal, description};
        alert('Cập nhật chương trình khuyến mãi thành công.');
    } else {
        const newId = promotions.length ? Math.max(...promotions.map(p => p.id)) + 1 : 1;
        promotions.push({id: newId, code, name, discountPercent, startDate, endDate, status: statusVal, description});
        alert('Tạo chương trình khuyến mãi mới thành công.');
    }
    renderPromotionTable();
    hideForm();
};
cancelBtn.onclick = () => {
    hideForm();
};
btnAddPromotion.onclick = () => {
    clearForm();
    showForm();
};
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

        if(section !== 'promotions'){
            mainHeading.textContent = link.textContent.trim();
            contentSection.innerHTML = '<p style="padding:1rem; color:#065f46; font-weight:600;">Chức năng ' + link.textContent.trim() + ' sẽ được phát triển tiếp theo.</p>';
        } else {
            mainHeading.textContent = 'Quản lý khuyến mãi';
            contentSection.innerHTML = `
            <button class="btn" id="btnAddPromotion">Tạo chương trình khuyến mãi</button>
            <table aria-label="Danh sách khuyến mãi" id="promotionTable">
              <thead>
                <tr>
                  <th>STT</th>
                  <th>Mã khuyến mãi</th>
                  <th>Tên chương trình</th>
                  <th>Giá trị giảm (%)</th>
                  <th>Ngày bắt đầu</th>
                  <th>Ngày kết thúc</th>
                  <th>Trạng thái</th>
                  <th>Mô tả</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody id="promotionTableBody"></tbody>
            </table>
            <form id="promotionForm" style="display:none; margin-top: 2rem;">
              <h2 id="formTitle" style="color:#064e3b; margin-bottom:1rem;">Tạo chương trình khuyến mãi</h2>
              <label for="promoCode">Mã khuyến mãi</label>
              <input type="text" id="promoCode" name="promoCode" required placeholder="KM001" pattern="^KM[0-9]{3,}$" title="Mã bắt đầu KM theo sau là số" />
              <label for="promoName">Tên chương trình</label>
              <input type="text" id="promoName" name="promoName" required placeholder="Khuyến mãi hè" />
              <label for="discountPercent">Giá trị giảm (%)</label>
              <input type="number" id="discountPercent" name="discountPercent" required min="0" max="100" />
              <label for="startDate">Ngày bắt đầu</label>
              <input type="date" id="startDate" name="startDate" required />
              <label for="endDate">Ngày kết thúc</label>
              <input type="date" id="endDate" name="endDate" required />
              <label for="status">Trạng thái</label>
              <select id="status" name="status" required>
                <option value="" disabled selected>Chọn trạng thái</option>
                <option value="Active">Hoạt động</option>
                <option value="Disabled">Vô hiệu hóa</option>
              </select>
              <label for="description">Mô tả</label>
              <textarea id="description" name="description" placeholder="Mô tả chi tiết chương trình"></textarea>
              <div style="grid-column: 2 / 3; margin-top: 1.5rem;">
                <button type="submit" class="btn" id="submitBtn">Lưu khuyến mãi</button>
                <button type="button" class="btn" id="cancelBtn" style="background:#ef4444; margin-left: 12px;">Hủy</button>
              </div>
            </form>
          `;
            setTimeout(setupPromotionFormJS);
        }
    });
});

// Setup JS form events when content is dynamically injected
function setupPromotionFormJS(){
    promotions = promotions || [];
    editingPromotionId = null;
    const promotionTableBodyDyn = document.getElementById('promotionTableBody');
    const promotionFormDyn = document.getElementById('promotionForm');
    const formTitleDyn = document.getElementById('formTitle');
    const submitBtnDyn = document.getElementById('submitBtn');
    const cancelBtnDyn = document.getElementById('cancelBtn');
    const btnAddPromotionDyn = document.getElementById('btnAddPromotion');

    const inputPromoCodeDyn = document.getElementById('promoCode');
    const inputPromoNameDyn = document.getElementById('promoName');
    const inputDiscountPercentDyn = document.getElementById('discountPercent');
    const inputStartDateDyn = document.getElementById('startDate');
    const inputEndDateDyn = document.getElementById('endDate');
    const selectStatusDyn = document.getElementById('status');
    const textareaDescriptionDyn = document.getElementById('description');

    function renderPromotionTableDyn(){
        promotionTableBodyDyn.innerHTML = '';
        promotions.forEach((promo,index) => {
            const tr = document.createElement('tr');
            tr.tabIndex = 0;
            tr.innerHTML = `
            <td>${index + 1}</td>
            <td>${promo.code}</td>
            <td>${promo.name}</td>
            <td>${promo.discountPercent}%</td>
            <td>${promo.startDate}</td>
            <td>${promo.endDate}</td>
            <td>${promo.status === "Active" ? "Hoạt động" : "Vô hiệu hóa"}</td>
            <td title="${promo.description}">${promo.description.length>30 ? promo.description.slice(0,30)+'...' : promo.description}</td>
            <td>
              <button class="btn btnEdit" data-id="${promo.id}" style="padding: 6px 12px; font-size: 0.8rem;">Sửa</button>
              <button class="btn btnDelete" data-id="${promo.id}" style="padding: 6px 12px; font-size: 0.8rem; background:#ef4444; box-shadow:none;">Xóa</button>
            </td>
          `;
            promotionTableBodyDyn.appendChild(tr);
        });
        attachTableButtonEventsDyn();
    }
    function clearFormDyn(){
        promotionFormDyn.reset();
        editingPromotionId = null;
        formTitleDyn.textContent = "Tạo chương trình khuyến mãi";
        submitBtnDyn.textContent = "Lưu khuyến mãi";
    }
    function showFormDyn(){
        promotionFormDyn.style.display = 'grid';
        btnAddPromotionDyn.style.display = 'none';
        window.scrollTo({top:document.body.scrollHeight,behavior:'smooth'});
        inputPromoCodeDyn.focus();
    }
    function hideFormDyn(){
        promotionFormDyn.style.display = 'none';
        btnAddPromotionDyn.style.display = 'inline-block';
        clearFormDyn();
    }
    function attachTableButtonEventsDyn(){
        document.querySelectorAll('.btnEdit').forEach(btn=>{
            btn.onclick = e=>{
                const id = Number(e.target.dataset.id);
                editPromotionDyn(id);
            };
        });
        document.querySelectorAll('.btnDelete').forEach(btn=>{
            btn.onclick = e=>{
                const id = Number(e.target.dataset.id);
                deletePromotionDyn(id);
            };
        });
    }
    function editPromotionDyn(id){
        const promo = promotions.find(p=>p.id===id);
        if(!promo) return;
        editingPromotionId = id;
        formTitleDyn.textContent = "Cập nhật khuyến mãi";
        submitBtnDyn.textContent = "Cập nhật";
        inputPromoCodeDyn.value = promo.code;
        inputPromoNameDyn.value = promo.name;
        inputDiscountPercentDyn.value = promo.discountPercent;
        inputStartDateDyn.value = promo.startDate;
        inputEndDateDyn.value = promo.endDate;
        selectStatusDyn.value = promo.status;
        textareaDescriptionDyn.value = promo.description;
        showFormDyn();
    }
    function deletePromotionDyn(id){
        if(confirm("Bạn có chắc chắn muốn xóa chương trình khuyến mãi này?")){
            promotions = promotions.filter(p=>p.id!==id);
            renderPromotionTableDyn();
        }
    }
    promotionFormDyn.onsubmit = e=>{
        e.preventDefault();
        const code = inputPromoCodeDyn.value.trim();
        const name = inputPromoNameDyn.value.trim();
        const discountPercent = Number(inputDiscountPercentDyn.value);
        const startDate = inputStartDateDyn.value;
        const endDate = inputEndDateDyn.value;
        const statusVal = selectStatusDyn.value;
        const description = textareaDescriptionDyn.value.trim();
        if(!code || !name || discountPercent===0 || !startDate || !endDate || !statusVal){
            alert("Vui lòng điền đầy đủ thông tin hợp lệ.");
            return;
        }
        if(new Date(endDate) < new Date(startDate)){
            alert("Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu.");
            return;
        }
        if(!editingPromotionId && promotions.some(p=>p.code===code)){
            alert("Mã khuyến mãi đã tồn tại.");
            return;
        }
        if(editingPromotionId){
            const idx = promotions.findIndex(p=>p.id===editingPromotionId);
            if(idx===-1) return;
            promotions[idx] = {id:editingPromotionId,code,name,discountPercent,startDate,endDate,status:statusVal,description};
            alert("Cập nhật chương trình khuyến mãi thành công.");
        } else {
            const newId = promotions.length ? Math.max(...promotions.map(p=>p.id)) + 1:1;
            promotions.push({id:newId,code,name,discountPercent,startDate,endDate,status:statusVal,description});
            alert("Tạo chương trình khuyến mãi mới thành công.");
        }
        renderPromotionTableDyn();
        hideFormDyn();
    };
    cancelBtnDyn.onclick = ()=>{
        hideFormDyn();
    };
    btnAddPromotionDyn.onclick = ()=>{
        clearFormDyn();
        showFormDyn();
    };
    renderPromotionTableDyn();
}

// Sidebar toggle for mobile
const mobileToggle = document.getElementById('mobileToggle');
const sidebar = document.getElementById('sidebar');
mobileToggle.addEventListener('click', () => {
    sidebar.classList.toggle('open');
});
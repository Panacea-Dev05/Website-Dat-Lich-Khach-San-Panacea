package panacea.website_dat_lich_khach_san.core.NhanVien.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import panacea.website_dat_lich_khach_san.entity.Customer;
import panacea.website_dat_lich_khach_san.entity.CustomerPreferences;
import panacea.website_dat_lich_khach_san.repository.CustomerPreferencesRepository;
import panacea.website_dat_lich_khach_san.repository.CustomerRepository;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.CustomerDTO;

@Service
public class ThongTinKhachHangService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerPreferencesRepository customerPreferencesRepository;

    // Lấy tên nhân viên
    public String getStaffName() {
        return "Nhân viên quản lý khách hàng";
    }

    // Lấy danh sách tất cả khách hàng
    public List<Customer> getAllCustomers() {
        List<Customer> list = customerRepository.findAll();
        return list != null ? list : new java.util.ArrayList<>();
    }

    // Lấy danh sách tất cả sở thích khách hàng
    public List<CustomerPreferences> getAllPreferences() {
        return customerPreferencesRepository.findAll();
    }

    // Thêm khách hàng mới
    public void addCustomer(CustomerDTO dto) {
        // Validate dữ liệu đầu vào
        if (dto.getHo() == null || dto.getHo().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ không được để trống");
        }
        if (dto.getTen() == null || dto.getTen().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên không được để trống");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        // Mật khẩu không bắt buộc khi nhân viên đăng ký cho khách hàng
        // Khách hàng sẽ tự tạo mật khẩu sau khi nhận được thông tin tài khoản

        // Kiểm tra email đã tồn tại chưa
        if (customerRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại trong hệ thống");
        }

        Customer customer = new Customer();
        customer.setHo(dto.getHo().trim());
        customer.setTen(dto.getTen().trim());
        customer.setEmail(dto.getEmail().trim());
        customer.setSoDienThoai(dto.getSoDienThoai() != null ? dto.getSoDienThoai().trim() : null);
        customer.setNgaySinh(dto.getNgaySinh()); // Có thể null
        customer.setGioiTinh(dto.getGioiTinh() != null ? Customer.GioiTinh.fromString(dto.getGioiTinh()) : null);
        customer.setQuocTich(dto.getQuocTich() != null ? dto.getQuocTich().trim() : null);
        customer.setDiaChi(dto.getDiaChi() != null ? dto.getDiaChi().trim() : null);
        // Set mật khẩu mặc định cho tài khoản được tạo bởi nhân viên
        customer.setMatKhauHash(dto.getMatKhauHash() != null && !dto.getMatKhauHash().trim().isEmpty() 
            ? dto.getMatKhauHash().trim() : "TEMP_PASSWORD_" + System.currentTimeMillis());
        customer.setMaKhachHang("KH" + System.currentTimeMillis());
        customer.setTrangThai(Customer.TrangThaiCustomer.HOAT_DONG);
        customerRepository.save(customer);
    }

    // Xóa khách hàng
    public void deleteCustomer(Integer id) {
        customerRepository.deleteById(id);
    }

    // Lấy thông tin khách hàng theo ID
    public CustomerDTO getCustomerById(Integer id) {
        var c = customerRepository.findById(id).orElse(null);
        if (c == null) return null;
        CustomerDTO dto = new CustomerDTO();
        dto.setId(c.getId());
        dto.setHo(c.getHo());
        dto.setTen(c.getTen());
        dto.setEmail(c.getEmail());
        dto.setSoDienThoai(c.getSoDienThoai());
        dto.setNgaySinh(c.getNgaySinh());
        dto.setGioiTinh(c.getGioiTinh() != null ? c.getGioiTinh().getLabel() : null);
        dto.setQuocTich(c.getQuocTich());
        dto.setDiaChi(c.getDiaChi());
        dto.setMatKhauHash(c.getMatKhauHash());
        dto.setMaKhachHang(c.getMaKhachHang());
        dto.setTrangThai(c.getTrangThai() != null ? c.getTrangThai().getValue() : null);
        return dto;
    }

    // Cập nhật thông tin khách hàng
    public void updateCustomer(CustomerDTO dto) {
        var c = customerRepository.findById(dto.getId()).orElse(null);
        if (c == null) {
            throw new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + dto.getId());
        }

        // Validate dữ liệu đầu vào
        if (dto.getHo() == null || dto.getHo().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ không được để trống");
        }
        if (dto.getTen() == null || dto.getTen().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên không được để trống");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        // Mật khẩu không bắt buộc khi cập nhật - nhân viên có thể chỉ cập nhật thông tin cơ bản
        // Nếu không cung cấp mật khẩu mới, sẽ giữ nguyên mật khẩu cũ

        // Kiểm tra email đã tồn tại chưa (trừ khách hàng hiện tại)
        var existingCustomer = customerRepository.findByEmail(dto.getEmail());
        if (existingCustomer.isPresent() && !existingCustomer.get().getId().equals(dto.getId())) {
            throw new IllegalArgumentException("Email đã tồn tại trong hệ thống");
        }

        c.setHo(dto.getHo().trim());
        c.setTen(dto.getTen().trim());
        c.setEmail(dto.getEmail().trim());
        c.setSoDienThoai(dto.getSoDienThoai() != null ? dto.getSoDienThoai().trim() : null);
        c.setNgaySinh(dto.getNgaySinh()); // Có thể null
        c.setGioiTinh(dto.getGioiTinh() != null ? Customer.GioiTinh.fromString(dto.getGioiTinh()) : null);
        c.setQuocTich(dto.getQuocTich() != null ? dto.getQuocTich().trim() : null);
        c.setDiaChi(dto.getDiaChi() != null ? dto.getDiaChi().trim() : null);
        // Chỉ cập nhật mật khẩu nếu có giá trị mới
        if (dto.getMatKhauHash() != null && !dto.getMatKhauHash().trim().isEmpty()) {
            c.setMatKhauHash(dto.getMatKhauHash().trim());
        }
        customerRepository.save(c);
    }
}
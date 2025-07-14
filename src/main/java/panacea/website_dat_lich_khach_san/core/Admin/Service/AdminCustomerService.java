package panacea.website_dat_lich_khach_san.core.Admin.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import panacea.website_dat_lich_khach_san.entity.Customer;
import panacea.website_dat_lich_khach_san.entity.CustomerPreferences;
import panacea.website_dat_lich_khach_san.entity.UserSession;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.CustomerDTO;
import panacea.website_dat_lich_khach_san.repository.CustomerPreferencesRepository;
import panacea.website_dat_lich_khach_san.repository.CustomerRepository;
import panacea.website_dat_lich_khach_san.repository.UserSessionRepository;

@Service
public class AdminCustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private CustomerPreferencesRepository customerPreferencesRepository;
    
    @Autowired
    private UserSessionRepository userSessionRepository;
    
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public CustomerDTO getCustomerById(Integer id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.map(this::convertToDTO).orElse(null);
    }
    
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        Customer customer = convertToEntity(customerDTO);
        
        // Tự động tạo mã khách hàng nếu chưa có
        if (customer.getMaKhachHang() == null || customer.getMaKhachHang().isEmpty()) {
            customer.setMaKhachHang("CUST" + UUID.randomUUID().toString().substring(0, 8));
        }
        
        // Tạo mật khẩu mặc định nếu chưa có
        if (customer.getMatKhauHash() == null || customer.getMatKhauHash().isEmpty()) {
            String defaultPassword = "123456"; // Mật khẩu mặc định
            String hash = Integer.toHexString(defaultPassword.hashCode());
            customer.setMatKhauHash(hash);
        }
        
        // Đặt trạng thái mặc định nếu chưa có
        if (customer.getTrangThai() == null) {
            customer.setTrangThai(Customer.TrangThaiCustomer.HOAT_DONG);
        }
        
        // Đặt điểm tích lũy mặc định
        if (customer.getDiemTichLuy() == null) {
            customer.setDiemTichLuy(0);
        }
        
        Customer savedCustomer = customerRepository.save(customer);
        return convertToDTO(savedCustomer);
    }
    
    public CustomerDTO updateCustomer(Integer id, CustomerDTO customerDTO) {
        Optional<Customer> existingCustomer = customerRepository.findById(id);
        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();
            customer.setHo(customerDTO.getHo());
            customer.setTen(customerDTO.getTen());
            customer.setEmail(customerDTO.getEmail());
            customer.setSoDienThoai(customerDTO.getSoDienThoai());
            customer.setDiaChi(customerDTO.getDiaChi());
            customer.setLoaiKhachHang(customerDTO.getLoaiKhachHang() != null ? panacea.website_dat_lich_khach_san.infrastructure.Enums.LoaiKhachHang.valueOf(customerDTO.getLoaiKhachHang()) : null);
            customer.setDiemTichLuy(customerDTO.getDiemTichLuy());
            customer.setMatKhauHash(customerDTO.getMatKhauHash());
            customer.setTrangThai(customerDTO.getTrangThai() != null ? panacea.website_dat_lich_khach_san.entity.Customer.TrangThaiCustomer.valueOf(customerDTO.getTrangThai()) : null);
            Customer savedCustomer = customerRepository.save(customer);
            return convertToDTO(savedCustomer);
        }
        return null;
    }
    
    public boolean deleteCustomer(Integer id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private CustomerDTO convertToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setMaKhachHang(customer.getMaKhachHang());
        dto.setHo(customer.getHo());
        dto.setTen(customer.getTen());
        dto.setEmail(customer.getEmail());
        dto.setSoDienThoai(customer.getSoDienThoai());
        dto.setNgaySinh(customer.getNgaySinh());
        dto.setGioiTinh(customer.getGioiTinh() != null ? customer.getGioiTinh().name() : null);
        dto.setQuocTich(customer.getQuocTich());
        dto.setSoCmndCccd(customer.getSoCmndCccd());
        dto.setSoHoChieu(customer.getSoHoChieu());
        dto.setDiaChi(customer.getDiaChi());
        dto.setLoaiKhachHang(customer.getLoaiKhachHang() != null ? customer.getLoaiKhachHang().name() : null);
        dto.setDiemTichLuy(customer.getDiemTichLuy());
        dto.setMatKhauHash(customer.getMatKhauHash());
        dto.setTrangThai(customer.getTrangThai() != null ? customer.getTrangThai().name() : null);
        dto.setUuidId(customer.getUuidId());
        dto.setCreatedDate(customer.getCreatedDate());
        dto.setLastModifiedDate(customer.getLastModifiedDate());
        return dto;
    }
    
    private Customer convertToEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setMaKhachHang(dto.getMaKhachHang());
        customer.setHo(dto.getHo());
        customer.setTen(dto.getTen());
        customer.setEmail(dto.getEmail());
        customer.setSoDienThoai(dto.getSoDienThoai());
        customer.setNgaySinh(dto.getNgaySinh());
        customer.setGioiTinh(dto.getGioiTinh() != null ? panacea.website_dat_lich_khach_san.entity.Customer.GioiTinh.fromString(dto.getGioiTinh()) : null);
        customer.setQuocTich(dto.getQuocTich());
        customer.setSoCmndCccd(dto.getSoCmndCccd());
        customer.setSoHoChieu(dto.getSoHoChieu());
        customer.setDiaChi(dto.getDiaChi());
        customer.setLoaiKhachHang(dto.getLoaiKhachHang() != null ? panacea.website_dat_lich_khach_san.infrastructure.Enums.LoaiKhachHang.valueOf(dto.getLoaiKhachHang()) : null);
        customer.setDiemTichLuy(dto.getDiemTichLuy());
        customer.setMatKhauHash(dto.getMatKhauHash());
        customer.setTrangThai(dto.getTrangThai() != null ? panacea.website_dat_lich_khach_san.entity.Customer.TrangThaiCustomer.valueOf(dto.getTrangThai()) : null);
        customer.setUuidId(dto.getUuidId());
        customer.setCreatedDate(dto.getCreatedDate());
        customer.setLastModifiedDate(dto.getLastModifiedDate());
        return customer;
    }
    
    // Đăng ký tài khoản khách hàng
    public CustomerDTO registerCustomer(CustomerDTO customerDTO, String rawPassword) {
        // Hash password (giả lập, nên dùng BCrypt thực tế)
        String hash = Integer.toHexString(rawPassword.hashCode());
        customerDTO.setMatKhauHash(hash);
        customerDTO.setTrangThai("HOAT_DONG");
        customerDTO.setMaKhachHang("CUST" + UUID.randomUUID().toString().substring(0, 8));
        return createCustomer(customerDTO);
    }
    
    // Đăng nhập khách hàng (trả về session token)
    public String loginCustomer(String email, String rawPassword, String ip, String userAgent) {
        List<Customer> customers = customerRepository.findAll().stream()
            .filter(c -> c.getEmail().equalsIgnoreCase(email))
            .toList();
        if (customers.isEmpty()) return null;
        Customer customer = customers.get(0);
        String hash = Integer.toHexString(rawPassword.hashCode());
        if (!customer.getMatKhauHash().equals(hash)) return null;
        // Tạo session (giả lập JWT bằng UUID)
        String token = UUID.randomUUID().toString();
        UserSession session = new UserSession();
        session.setUserId(customer.getId());
        session.setUserType(UserSession.UserType.CUSTOMER);
        session.setSessionToken(token);
        session.setIpAddress(ip);
        session.setUserAgent(userAgent);
        session.setThoiGianDangNhap(LocalDateTime.now());
        session.setThoiGianHetHan(LocalDateTime.now().plusHours(12));
        session.setTrangThai("Active");
        userSessionRepository.save(session);
        return token;
    }
    
    // Quản lý sở thích khách hàng
    public CustomerPreferences saveOrUpdatePreferences(Integer customerId, CustomerPreferences pref) {
        List<CustomerPreferences> existing = customerPreferencesRepository.findAll().stream()
            .filter(p -> p.getKhachHang() != null && p.getKhachHang().getId().equals(customerId))
            .toList();
        if (!existing.isEmpty()) {
            CustomerPreferences old = existing.get(0);
            old.setLoaiPhongUuThien(pref.getLoaiPhongUuThien());
            old.setTangUuThien(pref.getTangUuThien());
            old.setHuongNhinUuThien(pref.getHuongNhinUuThien());
            old.setYeuCauDacBiet(pref.getYeuCauDacBiet());
            old.setThoiGianNhanPhongMongMuon(pref.getThoiGianNhanPhongMongMuon());
            old.setDichVuUuThien(pref.getDichVuUuThien());
            old.setTinhTrangHutThuoc(pref.getTinhTrangHutThuoc());
            old.setCheDoAnUong(pref.getCheDoAnUong());
            return customerPreferencesRepository.save(old);
        } else {
            pref.setKhachHang(customerRepository.findById(customerId).orElse(null));
            return customerPreferencesRepository.save(pref);
        }
    }
    
    public CustomerPreferences getPreferences(Integer customerId) {
        return customerPreferencesRepository.findAll().stream()
            .filter(p -> p.getKhachHang() != null && p.getKhachHang().getId().equals(customerId))
            .findFirst().orElse(null);
    }
} 
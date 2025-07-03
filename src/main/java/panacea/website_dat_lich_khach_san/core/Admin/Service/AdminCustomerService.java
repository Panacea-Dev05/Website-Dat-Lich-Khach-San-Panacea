package panacea.website_dat_lich_khach_san.core.Admin.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Customer;
import panacea.website_dat_lich_khach_san.repository.CustomerRepository;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.CustomerDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminCustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
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
} 
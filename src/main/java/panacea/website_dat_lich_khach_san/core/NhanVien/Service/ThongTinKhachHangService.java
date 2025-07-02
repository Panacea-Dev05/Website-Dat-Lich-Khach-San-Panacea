package panacea.website_dat_lich_khach_san.core.NhanVien.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import panacea.website_dat_lich_khach_san.entity.Customer;
import panacea.website_dat_lich_khach_san.entity.CustomerPreferences;
import panacea.website_dat_lich_khach_san.repository.CustomerPreferencesRepository;
import panacea.website_dat_lich_khach_san.repository.CustomerRepository;

@Service
public class ThongTinKhachHangService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerPreferencesRepository customerPreferencesRepository;

    public String getStaffName() {
        return "Nguyễn Văn A";
    }

    public List<Customer> getAllCustomers() {
        List<Customer> list = customerRepository.findAll();
        return list != null ? list : new java.util.ArrayList<>();
    }

    public List<CustomerPreferences> getAllPreferences() {
        return customerPreferencesRepository.findAll();
    }
} 
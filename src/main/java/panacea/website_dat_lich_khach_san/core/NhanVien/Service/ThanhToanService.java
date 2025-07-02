package panacea.website_dat_lich_khach_san.core.NhanVien.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Payment;
import panacea.website_dat_lich_khach_san.repository.PaymentRepository;

import java.util.List;

@Service
public class ThanhToanService {
    @Autowired
    private PaymentRepository paymentRepository;

    public String getStaffName() {
        return "Nguyễn Văn A";
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
} 
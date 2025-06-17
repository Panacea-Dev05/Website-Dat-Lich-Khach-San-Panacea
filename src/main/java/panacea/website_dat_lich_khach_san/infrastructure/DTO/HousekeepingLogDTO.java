package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class HousekeepingLogDTO {
    private Integer id;
    private Integer phongId;
    private Integer nhanVienId;
    private LocalDate ngayDonPhong;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;
    private String trangThaiTruoc;
    private String trangThaiSau;
    private String vanDePhatHien;
    private String danhSachVatPham;
    private Byte chatLuongDonPhong;
    private String ghiChu;
    private String hinhAnhTruoc;
    private String hinhAnhSau;
    private UUID uuidId;
    private Long createdDate;
} 
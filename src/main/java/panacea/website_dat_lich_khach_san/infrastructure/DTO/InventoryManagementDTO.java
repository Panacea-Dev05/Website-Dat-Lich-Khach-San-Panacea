package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryManagementDTO {
    private Integer id;
    private Integer khachSanId;
    private String tenVatPham;
    private String maVatPham;
    private String loaiVatPham;
    private String donViTinh;
    private Short soLuongTon;
    private Short soLuongToiThieu;
    private BigDecimal giaNhap;
    private String nhaCungCap;
    private LocalDate ngayNhapCuoi;
    private LocalDate hanSuDung;
    private String viTriKho;
    private String trangThai;
    private UUID uuidId;
} 
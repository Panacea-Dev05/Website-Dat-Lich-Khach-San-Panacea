package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryManagementDTO {
    private Integer id;
    @NotNull(message = "khachSanId không được để trống")
    private Integer khachSanId;
    @NotBlank(message = "Tên vật phẩm không được để trống")
    @Size(max = 100, message = "Tên vật phẩm tối đa 100 ký tự")
    private String tenVatPham;
    @NotBlank(message = "Mã vật phẩm không được để trống")
    @Size(max = 20, message = "Mã vật phẩm tối đa 20 ký tự")
    private String maVatPham;
    @NotBlank(message = "Loại vật phẩm không được để trống")
    @Size(max = 50, message = "Loại vật phẩm tối đa 50 ký tự")
    private String loaiVatPham;
    @NotBlank(message = "Đơn vị tính không được để trống")
    @Size(max = 20, message = "Đơn vị tính tối đa 20 ký tự")
    private String donViTinh;
    @NotNull(message = "Số lượng tồn không được để trống")
    @Min(value = 0, message = "Số lượng tồn phải >= 0")
    private Short soLuongTon;
    @NotNull(message = "Số lượng tối thiểu không được để trống")
    @Min(value = 0, message = "Số lượng tối thiểu phải >= 0")
    private Short soLuongToiThieu;
    @NotNull(message = "Giá nhập không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá nhập phải >= 0")
    private BigDecimal giaNhap;
    @NotBlank(message = "Nhà cung cấp không được để trống")
    @Size(max = 100, message = "Nhà cung cấp tối đa 100 ký tự")
    private String nhaCungCap;
    private LocalDate ngayNhapCuoi;
    private LocalDate hanSuDung;
    @NotBlank(message = "Vị trí kho không được để trống")
    @Size(max = 50, message = "Vị trí kho tối đa 50 ký tự")
    private String viTriKho;
    @NotBlank(message = "Trạng thái không được để trống")
    @Size(max = 20, message = "Trạng thái tối đa 20 ký tự")
    private String trangThai;
    private UUID uuidId;
} 
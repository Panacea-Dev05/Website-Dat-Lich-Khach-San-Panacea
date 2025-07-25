package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import jakarta.validation.constraints.*;

@Data
public class PromotionDTO {
    private Integer id;
    @NotBlank(message = "Mã khuyến mãi không được để trống")
    @Size(max = 20, message = "Mã khuyến mãi tối đa 20 ký tự")
    private String maKhuyenMai;
    @NotBlank(message = "Tên khuyến mãi không được để trống")
    @Size(max = 100, message = "Tên khuyến mãi tối đa 100 ký tự")
    private String tenKhuyenMai;
    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String moTa;
    @NotBlank(message = "Loại giảm giá không được để trống")
    private String loaiGiamGia;
    @NotNull(message = "Giá trị giảm không được để trống")
    @DecimalMin(value = "0.01", message = "Giá trị giảm phải lớn hơn 0")
    private BigDecimal giaTriGiam;
    @DecimalMin(value = "0", message = "Giảm tối đa phải >= 0")
    private BigDecimal giamToiDa;
    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate ngayBatDau;
    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate ngayKetThuc;
    @NotNull(message = "Số lượng tối đa không được để trống")
    @Min(value = 1, message = "Số lượng tối đa phải >= 1")
    private Integer soLuongToiDa;
    private Integer daSuDung;
    @Size(max = 500, message = "Điều kiện áp dụng tối đa 500 ký tự")
    private String dieuKienApDung;
    @NotBlank(message = "Trạng thái không được để trống")
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
    private Long lastModifiedDate;
} 
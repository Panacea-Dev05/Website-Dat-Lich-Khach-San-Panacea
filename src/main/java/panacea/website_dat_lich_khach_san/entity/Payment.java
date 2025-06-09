package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PAYMENT")

public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_thanh_toan", unique = true, nullable = false)
    private String maThanhToan;

    @ManyToOne
    @JoinColumn(name = "dat_phong_id", nullable = false)
    private Booking datPhong;

    @Column(name = "so_tien", nullable = false)
    private BigDecimal soTien;

    @Column(name = "phuong_thuc", nullable = false)
    private String phuongThuc;

    @Column(name = "ma_giao_dich")
    private String maGiaoDich;

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    @Column(name = "noi_dung")
    private String noiDung;

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;
}

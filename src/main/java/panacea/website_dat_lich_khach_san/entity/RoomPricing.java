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
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ROOM_PRICING")

public class RoomPricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "loai_phong_id", nullable = false)
    private RoomTypes loaiPhong;

    @Column(name = "loai_gia")
    private String loaiGia;

    @Column(name = "gia_tri")
    private BigDecimal giaTri;

    @Column(name = "ngay_bat_dau")
    private java.sql.Date ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private java.sql.Date ngayKetThuc;

    @Column(name = "ap_dung_cho")
    private String apDungCho = "All";

    @Column(name = "he_so_dieu_chinh")
    private BigDecimal heSoDieuChinh = BigDecimal.valueOf(1.00);

    @Column(name = "trang_thai")
    private String trangThai = "Hoạt động";

    @Column(name = "uuid_id")
    private UUID uuidId = UUID.randomUUID();

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;
}

package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CUSTOMER_PREFERENCES")

public class CustomerPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "khach_hang_id", nullable = false, unique = true)
    private Customer khachHang;

    @ManyToOne
    @JoinColumn(name = "loai_phong_uu_thien")
    private RoomTypes loaiPhongUuThien;

    @Column(name = "tang_uu_thien")
    private String tangUuThien;

    @Column(name = "huong_nhin_uu_thien")
    private String huongNhinUuThien;

    @Column(name = "yeu_cau_dac_biet", columnDefinition = "NVARCHAR(MAX)")
    private String yeuCauDacBiet;

    @Column(name = "thoi_gian_nhan_phong_mong_muon")
    private LocalTime thoiGianNhanPhongMongMuon;

    @Column(name = "dich_vu_uu_thien", columnDefinition = "NVARCHAR(MAX)")
    private String dichVuUuThien;

    @Column(name = "tinh_trang_hut_thuoc")
    private String tinhTrangHutThuoc;

    @Column(name = "che_do_an_uong")
    private String cheDoAnUong;

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;
}

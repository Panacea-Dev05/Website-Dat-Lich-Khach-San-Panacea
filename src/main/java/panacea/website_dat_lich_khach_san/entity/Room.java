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
@Table(name = "ROOM")

public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "so_phong", nullable = false)
    private String soPhong;

    // DO phần tui ko có 2 cái entity này
//    @ManyToOne
//    @JoinColumn(name = "khach_san_id", nullable = false)
//    private Hotel khachSan;
//
//    @ManyToOne
//    @JoinColumn(name = "loai_phong_id", nullable = false)
//    private RoomType loaiPhong;

    @Column(name = "tang")
    private Byte tang;

    @Column(name = "view_phong")
    private String viewPhong;

    @Column(name = "trang_thai")
    private String trangThai = "Sẵn sàng";

    @Column(name = "gia_co_ban")
    private BigDecimal giaCoBan;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "uuid_id")
    private UUID uuidId = UUID.randomUUID();

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;
}

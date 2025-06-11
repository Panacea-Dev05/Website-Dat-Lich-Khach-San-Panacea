package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "ROOM_TYPE")

public class RoomTypes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_loai_phong", nullable = false, unique = true)
    private String maLoaiPhong;

    @Column(name = "ten_loai_phong", nullable = false)
    private String tenLoaiPhong;

    @Column(name = "dien_tich")
    private BigDecimal dienTich;

    @Column(name = "so_giuong")
    private Byte soGiuong;

    @Column(name = "loai_giuong")
    private String loaiGiuong;

    @Column(name = "suc_chua_toi_da")
    private Byte sucChuaToiDa;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "tien_nghi")
    private String tienNghi;

    @Column(name = "uuid_id")
    private UUID uuidId = UUID.randomUUID();

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;
}


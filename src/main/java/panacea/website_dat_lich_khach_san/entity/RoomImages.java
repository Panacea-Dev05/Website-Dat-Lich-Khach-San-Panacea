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

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ROOM_IMAGES")

public class RoomImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "phong_id")
    private Room phong;

    @ManyToOne
    @JoinColumn(name = "loai_phong_id")
    private RoomTypes loaiPhong;

    @Column(name = "url_hinh_anh", nullable = false)
    private String urlHinhAnh;

    @Column(name = "ten_hinh_anh")
    private String tenHinhAnh;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "thu_tu_hien_thi")
    private Byte thuTuHienThi = 1;

    @Column(name = "la_hinh_chinh")
    private Boolean laHinhChinh = false;

    @Column(name = "trang_thai")
    private String trangThai = "Hoạt động";

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;
}

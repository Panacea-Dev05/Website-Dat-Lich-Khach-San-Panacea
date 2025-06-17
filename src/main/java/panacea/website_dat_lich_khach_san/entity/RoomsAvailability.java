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
@Table(name = "ROOM_AVAILABILITY")

public class RoomsAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "phong_id", nullable = false)
    private Room phong;

    @Column(name = "ngay", nullable = false)
    private java.sql.Date ngay;

    @Column(name = "trang_thai")
    private String trangThai = "Available";

    @Column(name = "gia_ban")
    private BigDecimal giaBan;

    @Column(name = "so_phong_con_lai")
    private Byte soPhongConLai = 1;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;
}

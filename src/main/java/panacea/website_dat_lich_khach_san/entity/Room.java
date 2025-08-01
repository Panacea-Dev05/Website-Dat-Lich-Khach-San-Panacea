package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ROOM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "so_phong", length = 10, nullable = false)
    private String soPhong;

    @Column(name = "tang")
    private Byte tang;

    @Column(name = "view_phong", length = 50)
    private String viewPhong;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20)
    private TrangThaiPhong trangThai = TrangThaiPhong.SAN_SANG;

    @Column(name = "gia_co_ban", precision = 12, scale = 2)
    private BigDecimal giaCoBan;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loai_phong_id", nullable = false)
    private RoomType roomType;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingDetail> bookingDetails;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomAvailability> availabilities;

    // Enums
    public enum TrangThaiPhong {
        SAN_SANG("Sẵn sàng"),
        DA_DAT("Đã đặt"),
        DANG_SU_DUNG("Đang sử dụng"),
        BAO_TRI("Bảo trì"),
        DON_DEP("Dọn dẹp");

        private final String value;

        TrangThaiPhong(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.uuidId == null) {
            this.uuidId = UUID.randomUUID();
        }
        if (this.createdDate == null) {
            this.createdDate = System.currentTimeMillis();
        }
        if (this.lastModifiedDate == null) {
            this.lastModifiedDate = System.currentTimeMillis();
        }
        if (this.trangThai == null) {
            this.trangThai = TrangThaiPhong.SAN_SANG;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = System.currentTimeMillis();
    }
}
package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "ROOM_AVAILABILITY", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"phong_id", "ngay"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "phong_id", nullable = false)
    private Integer phongId;

    @Column(name = "ngay", nullable = false)
    private LocalDate ngay;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20)
    private TrangThaiAvailability trangThai = TrangThaiAvailability.Available;

    @Column(name = "gia_ban", precision = 12, scale = 2)
    private BigDecimal giaBan;

    @Column(name = "so_phong_con_lai")
    private Byte soPhongConLai = 1;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", insertable = false, updatable = false)
    private Room room;

    // Enums
    public enum TrangThaiAvailability {
        Available, Booked, Blocked, Maintenance
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
        if (this.soPhongConLai == null) {
            this.soPhongConLai = 1;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = System.currentTimeMillis();
    }
}
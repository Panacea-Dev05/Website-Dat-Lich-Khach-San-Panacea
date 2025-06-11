package panacea.website_dat_lich_khach_san.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "BOOKING_DETAIL", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"dat_phong_id", "phong_id"})
})
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "dat_phong_id", nullable = false)
    private Integer datPhongId;

    @Column(name = "phong_id", nullable = false)
    private Integer phongId;

    @Column(name = "gia_phong_thuc_te", precision = 12, scale = 2)
    private BigDecimal giaPhongThucTe;

    @Column(name = "ghi_chu", length = 200)
    private String ghiChu;

    @Column(name = "uuid_id", columnDefinition = "uniqueidentifier")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dat_phong_id", insertable = false, updatable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", insertable = false, updatable = false)
    private Room room;

    @PrePersist
    public void prePersist() {
        if (this.uuidId == null) {
            this.uuidId = UUID.randomUUID();
        }
        if (this.createdDate == null) {
            this.createdDate = System.currentTimeMillis();
        }
    }
}
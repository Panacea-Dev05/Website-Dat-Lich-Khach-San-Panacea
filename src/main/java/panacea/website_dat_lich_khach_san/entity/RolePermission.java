package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "ROLE_PERMISSION", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"vai_tro", "quyen_id"})
})

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "vai_tro", length = 50, nullable = false)
    private String vaiTro;

    @Column(name = "quyen_id", nullable = false)
    private Integer quyenId;

    @Column(name = "trang_thai", length = 20)
    private String trangThai = "Hoạt động";

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quyen_id", insertable = false, updatable = false)
    private Permission permission;

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
package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "PERMISSION")
@Data

@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma_quyen", length = 50, nullable = false, unique = true)
    private String maQuyen;

    @Column(name = "ten_quyen", length = 100, nullable = false)
    private String tenQuyen;

    @Column(name = "mo_ta", length = 200)
    private String moTa;

    @Column(name = "nhom_quyen", length = 50)
    private String nhomQuyen;

    @Column(name = "trang_thai", length = 20)
    private String trangThai = "Hoạt động";

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    // Relationships
    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RolePermission> rolePermissions;

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
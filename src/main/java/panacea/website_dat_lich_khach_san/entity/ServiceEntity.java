package panacea.website_dat_lich_khach_san.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "SERVICE")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma_dich_vu", length = 20, nullable = false, unique = true)
    private String maDichVu;

    @Column(name = "ten_dich_vu", length = 100, nullable = false)
    private String tenDichVu;

    @Column(name = "loai_dich_vu", length = 50, nullable = false)
    private String loaiDichVu;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "don_gia", precision = 12, scale = 2)
    private BigDecimal donGia;

    @Column(name = "don_vi_tinh", length = 20)
    private String donViTinh;

    @Column(name = "trang_thai", length = 20)
    private String trangThai = "Hoạt động";

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    // Relationships
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ServiceDetail> serviceDetails;

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
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = System.currentTimeMillis();
    }
}
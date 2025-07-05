package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "AUDIT_LOG")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "bang_tac_dong", length = 50, nullable = false)
    private String bangTacDong;

    @Column(name = "id_ban_ghi", nullable = false)
    private Integer idBanGhi;

    @Enumerated(EnumType.STRING)
    @Column(name = "hanh_dong", length = 20)
    private HanhDong hanhDong;

    @Column(name = "du_lieu_cu", columnDefinition = "NVARCHAR(MAX)")
    private String duLieuCu; // JSON

    @Column(name = "du_lieu_moi", columnDefinition = "NVARCHAR(MAX)")
    private String duLieuMoi; // JSON

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_type", length = 20)
    private String userType;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "thoi_gian_thuc_hien")
    private LocalDateTime thoiGianThucHien;

    @Column(name = "ly_do_thay_doi", length = 500)
    private String lyDoThayDoi;

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    // Enums
    public enum HanhDong {
        INSERT, UPDATE, DELETE
    }

    @PrePersist
    public void prePersist() {
        if (this.uuidId == null) {
            this.uuidId = UUID.randomUUID();
        }
        if (this.createdDate == null) {
            this.createdDate = System.currentTimeMillis();
        }
        if (this.thoiGianThucHien == null) {
            this.thoiGianThucHien = LocalDateTime.now();
        }
    }
}
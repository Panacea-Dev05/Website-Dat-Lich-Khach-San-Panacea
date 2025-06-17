package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "CUSTOMER_COMMUNICATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCommunication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_hang_id", nullable = false)
    private Customer khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dat_phong_id")
    private Booking datPhong;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_lien_lac", length = 20)
    private LoaiLienLac loaiLienLac;

    @Column(name = "noi_dung", columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    @Column(name = "tieu_de", length = 200)
    private String tieuDe;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20)
    private TrangThaiLienLac trangThai = TrangThaiLienLac.SENT;

    @Column(name = "ngay_gui")
    private LocalDateTime ngayGui;

    @Column(name = "ngay_doc")
    private LocalDateTime ngayDoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_gui")
    private Staff nguoiGui;

    @Column(name = "template_id", length = 50)
    private String templateId;

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    // Enums
    public enum LoaiLienLac {
        EMAIL("Email"),
        SMS("SMS"),
        PUSH("Push"),
        CALL("Call");

        private final String value;

        LoaiLienLac(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum TrangThaiLienLac {
        SENT("Sent"),
        DELIVERED("Delivered"),
        READ("Read"),
        FAILED("Failed");

        private final String value;

        TrangThaiLienLac(String value) {
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
        if (this.ngayGui == null) {
            this.ngayGui = LocalDateTime.now();
        }
        if (this.trangThai == null) {
            this.trangThai = TrangThaiLienLac.SENT;
        }
    }
}
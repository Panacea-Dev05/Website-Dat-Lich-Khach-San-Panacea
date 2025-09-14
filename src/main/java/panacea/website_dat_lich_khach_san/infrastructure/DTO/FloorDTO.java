package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FloorDTO {
    private Byte soTang;
    private Integer tongSoPhong;
    private Integer soPhongTrong;
    private Integer soPhongDangSuDung;
    private Integer soPhongDaDat;
    private Integer soPhongBaoTri;
    private Integer soPhongDonDep;
    
    // Constructor để tính toán từ dữ liệu Room
    public FloorDTO(Byte soTang, Integer tongSoPhong) {
        this.soTang = soTang;
        this.tongSoPhong = tongSoPhong;
        this.soPhongTrong = 0;
        this.soPhongDangSuDung = 0;
        this.soPhongDaDat = 0;
        this.soPhongBaoTri = 0;
        this.soPhongDonDep = 0;
    }
    
    // Phương thức tính tỷ lệ phần trăm phòng trống
    public Double getTyLePhongTrong() {
        if (tongSoPhong == 0) return 0.0;
        return (soPhongTrong.doubleValue() / tongSoPhong.doubleValue()) * 100;
    }
    
    // Phương thức tính tỷ lệ phần trăm phòng đang sử dụng
    public Double getTyLePhongDangSuDung() {
        if (tongSoPhong == 0) return 0.0;
        return (soPhongDangSuDung.doubleValue() / tongSoPhong.doubleValue()) * 100;
    }
}
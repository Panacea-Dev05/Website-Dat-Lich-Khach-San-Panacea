package panacea.website_dat_lich_khach_san.infrastructure.Enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = LoaiGiaoDichConverter.Deserializer.class)
public enum LoaiGiaoDich {
    NHAP_KHO("Nhập kho"),
    XUAT_KHO("Xuất kho"),
    DIEU_CHINH("Điều chỉnh"),
    KIEM_KE("Kiểm kê"),
    HUY_BO("Hủy bỏ");

    private final String value;

    LoaiGiaoDich(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String value() {
        return value;
    }
}
package panacea.website_dat_lich_khach_san.infrastructure.Enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = TrangThaiYeuCauConverter.Serializer.class)
@JsonDeserialize(using = TrangThaiYeuCauConverter.Deserializer.class)
public enum TrangThaiYeuCau {
    CHO_DUYET("Chờ duyệt"),
    DA_DUYET("Đã duyệt"),
    TU_CHOI("Từ chối"),
    DA_THUC_HIEN("Đã thực hiện");

    private final String value;

    TrangThaiYeuCau(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String value() {
        return value;
    }
}
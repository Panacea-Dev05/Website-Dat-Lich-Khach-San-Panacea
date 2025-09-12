package panacea.website_dat_lich_khach_san.infrastructure.Enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TrangThaiDuyet {
    CHO_DUYET("CHO_DUYET", "Chờ duyệt"),
    DA_DUYET("DA_DUYET", "Đã duyệt"),
    TU_CHOI("TU_CHOI", "Từ chối");

    private final String value;
    private final String displayName;

    TrangThaiDuyet(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static TrangThaiDuyet fromValue(String value) {
        for (TrangThaiDuyet status : TrangThaiDuyet.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
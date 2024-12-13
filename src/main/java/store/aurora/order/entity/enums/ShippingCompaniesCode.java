package store.aurora.order.entity.enums;

import lombok.Getter;

@Getter
public enum ShippingCompaniesCode {
    KOREA_POST("01", "우체국 택배"),
    CJ_LOGISTICS("04", "CJ대한통운"),
    LOGEN("06", "로젠 택배"),
    HANJIN("05", "한진 택배"),
    LOTTET_LOGISTICS("08", "롯데 택배"),
    CU_POST("46", "CU 편의점 택배"),
    CVS_NET("23", "CVSnet 편의점 택배"),
    // 추가 택배사 코드 필요시 아래에 추가
    ;

    private final String code;
    private final String description;

    ShippingCompaniesCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ShippingCompaniesCode fromCode(String code) {
        for (ShippingCompaniesCode company : values()) {
            if (company.getCode().equals(code)) {
                return company;
            }
        }
        throw new IllegalArgumentException("Invalid shipping company code: " + code);
    }
}

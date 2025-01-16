package store.aurora.coupon.dto;

import lombok.Data;

@Data
public class PaymentCouponDTO {
    private Long id;        //사용자 쿠폰 ID
    private String couponName;  //쿠폰명
    private Integer needCost;   //필요한 가격
    private Integer maxSale;    //최대 할인가
    private Integer salePercent;    //할인률
    private Integer saleAmount;     //할인값
}

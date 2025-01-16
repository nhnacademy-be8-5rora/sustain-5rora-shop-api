package store.aurora.coupon.feignclient;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.aurora.coupon.dto.PaymentCouponDTO;
import store.aurora.coupon.dto.ProductInfoDTO;


import java.util.List;
import java.util.Map;

@FeignClient(name = "couponClient", url = "${api.gateway.base-url}" + "/api/coupon/shop")
public interface CouponClient {

    //환불시에 refund controller 작동(if문으로 해당 refund 상품하는 결제 내역에 쿠폰이 있다면 작동하게끔)
    @PostMapping("/refund")
    void refund(@RequestBody List<Long> couponIds);

    //쿠폰 사용시 결제 버튼에서 이것도 결제 내역에 쿠폰이 있다면 발동하게끔.
    @PostMapping("/using")
    void used(@RequestBody List<Long> couponIds);

    // 사용 가능한 쿠폰 정보 전달
    @PostMapping("/usable")
    Map<Long, List<PaymentCouponDTO>> getCouponListByCategory(
            @RequestParam("userId") @Valid String userId,
            @RequestBody @Validated List<ProductInfoDTO> productInfoDTO);
}

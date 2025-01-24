package store.aurora.coupon;

import org.junit.jupiter.api.Test;
import store.aurora.coupon.dto.PaymentCouponDTO;

import static org.junit.jupiter.api.Assertions.*;

class PaymentCouponDTOTest {

    @Test
    void testPaymentCouponDTO() {
        // PaymentCouponDTO 객체 생성
        PaymentCouponDTO paymentCoupon = new PaymentCouponDTO();

        // Setter를 사용하여 값 설정
        paymentCoupon.setId(1L);
        paymentCoupon.setCouponName("Discount10");
        paymentCoupon.setNeedCost(5000);
        paymentCoupon.setMaxSale(1000);
        paymentCoupon.setSalePercent(10);
        paymentCoupon.setSaleAmount(500);

        // Getter를 사용하여 값 확인
        assertEquals(1L, paymentCoupon.getId());
        assertEquals("Discount10", paymentCoupon.getCouponName());
        assertEquals(5000, paymentCoupon.getNeedCost());
        assertEquals(1000, paymentCoupon.getMaxSale());
        assertEquals(10, paymentCoupon.getSalePercent());
        assertEquals(500, paymentCoupon.getSaleAmount());
    }
}

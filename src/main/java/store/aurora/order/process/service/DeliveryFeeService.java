package store.aurora.order.process.service;

public interface DeliveryFeeService {
    /**
     * 배송비 계산
     * 배송비 정책에 의거해 배송비 계산
     * <pre> 현재: 총 주문 금액이 30,000원 이상일 경우 배송비 무료 </pre>
     * @param totalAmount 쿠폰을 적용한 총 주문 금액 ( 포인트 적용 전 )
     * @return 배송비
     */
    int getDeliveryFee(int totalAmount);
}

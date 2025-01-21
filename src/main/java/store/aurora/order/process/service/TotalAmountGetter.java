package store.aurora.order.process.service;

import store.aurora.order.dto.OrderDetailDTO;

import java.util.List;

public interface TotalAmountGetter {

    /**
     * 주문 상세 정보를 통해 주문 총 금액 계산 (결제 금액은 포인트 사용량 포함해야 함)
     * @param orderDetailList 주문 상세 정보
     * @return totalAmount 총 금액
     */
    int getTotalAmountFromOrderDetailList(List<OrderDetailDTO> orderDetailList);
}

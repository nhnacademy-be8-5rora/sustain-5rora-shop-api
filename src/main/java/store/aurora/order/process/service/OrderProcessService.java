package store.aurora.order.process.service;

import store.aurora.order.dto.*;
import store.aurora.user.entity.User;

import java.util.List;

public interface OrderProcessService {

    /**
     * 주문 번호 uuid 생성
     * @return uuid 주문 번호
     */
    String getOrderUuid();

    /**
     * 주문 상세 정보를 통해 주문 총 금액 계산 (결제 금액은 포인트 사용량 포함해야 함)
     * @param orderDetailList 주문 상세 정보
     * @return totalAmount 총 금액
     */
    int getTotalAmountFromOrderDetailList(List<OrderDetailDTO> orderDetailList);

    /**
     * 주문 정보를 uuid를 key로 하여 redis에 저장
     * @param uuid 주문 번호
     * @param orderInfo 주문 정보
     */
    void saveOrderInfoInRedisWithUuid(String uuid, OrderRequestDto orderInfo);

    /**
     * uuid를 key로 하여 redis에서 주문 정보를 가져옴
     * OrderResponseDto 생성
     * <pre>
     *     <b>customerKey</b>   : UUID 생성
     *     <b>currency</b>      : 통화 ( KRW )
     *     <b>amount</b>        : OrderDetailDtoList를 통해서 생성
     *     <b>orderName</b>     : OrderDetailDtoList를 통해서 생성
     * </pre>
     * <pre>
     *     <b>확장상 고려</b>      : 통화 정보 (currency) parameter로 받아서 생성
     * </pre>
     * @param uuid 주문 번호 ( redis-key )
     * @return responseDto
     */
    OrderResponseDto getOrderResponseFromOrderRequestDtoInRedis(String uuid);

    /**
     * 사용자 주문 내역 저장
     * @param order OrderDTO 주문 정보
     * @param orderDetailList List<OrderDetailDTO> 주문 상세 정보
     * @param receiverInfo ReceiverInfoDTO 수령인 정보
     * @param user User 사용자 정보
     * @param orderedPersonInfo OrderedPersonInfoDTO 주문자 정보
     */
    void userOrderProcess(OrderDTO order, List<OrderDetailDTO> orderDetailList, ReceiverInfoDTO receiverInfo, User user, OrderedPersonInfoDTO orderedPersonInfo);
    /**
     * 비사용자 주문 내역 저장
     * @param order OrderDTO 주문 정보
     * @param orderDetailList List<OrderDetailDTO> 주문 상세 정보
     * @param receiverInfo ReceiverInfoDTO 수령인 정보
     * @param orderedPersonInfo OrderedPersonInfoDTO 주문자 정보
     */
    void nonUserOrderProcess(OrderDTO order, List<OrderDetailDTO> orderDetailList, ReceiverInfoDTO receiverInfo, OrderedPersonInfoDTO orderedPersonInfo);

}

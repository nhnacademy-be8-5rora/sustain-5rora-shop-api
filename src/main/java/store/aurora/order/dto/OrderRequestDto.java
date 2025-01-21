package store.aurora.order.dto;


import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderRequestDto {
    //인증정보
    private String username;
    private String nonMemberPassword;

    // 주문자 정보
    private String ordererName;
    private String ordererPhone;
    private String ordererEmail;
    private LocalDate preferredDeliveryDate;

    // 받는 사람 정보
    private String receiverName;
    private String receiverPhone;
    private String receiverEmail;
    private String receiverAddress;
    private String receiverMessage;

    //상품 정보
    private List<OrderDetailDTO> orderDetailDTOList;

    // 사용한 포인트
    private int usedPoint;
}
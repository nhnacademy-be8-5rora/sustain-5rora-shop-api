package store.aurora.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class OrderWithOrderDetailResponse {
    private OrderRelatedInfoDto orderRelatedInfoDto;
    private List<OrderDetailInfoDto> orderDetailInfoDtoList;
}

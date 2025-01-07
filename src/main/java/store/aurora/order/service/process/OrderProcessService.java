package store.aurora.order.service.process;

import store.aurora.order.dto.OrderDTO;
import store.aurora.order.dto.OrderDetailDTO;
import store.aurora.order.dto.OrderedPersonInfoDTO;
import store.aurora.order.dto.ReceiverInfoDTO;
import store.aurora.user.entity.User;

import java.util.List;
import java.util.Map;

public interface OrderProcessService {
    int getDeliveryFee(int totalAmount);
    int getTotalAmountFromOrderDetailList(List<OrderDetailDTO> orderDetailList);
    void userOrderProcess(OrderDTO order, List<OrderDetailDTO> orderDetailList, ReceiverInfoDTO receiverInfo, User user, OrderedPersonInfoDTO orderedPersonInfo);
    void nonUserOrderProcess(OrderDTO order, List<OrderDetailDTO> orderDetailList, ReceiverInfoDTO receiverInfo, OrderedPersonInfoDTO orderedPersonInfo);
    String getOrderUuid();
    void orderInfoSaveInRedisWithUuid(String uuid, Map<String, Object> orderInfo);
    Map<String, Object> getOrderInfoFromRedis(String uuid);
}

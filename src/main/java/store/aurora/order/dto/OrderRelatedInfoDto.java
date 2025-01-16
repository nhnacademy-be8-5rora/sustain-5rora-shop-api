package store.aurora.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.ShipmentInformation;
import store.aurora.order.entity.enums.OrderState;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRelatedInfoDto {
    private Long orderId;
    private LocalDate prefferedDeliveryDate;
    private Integer deliveryFee;
    private LocalDateTime orderTime;
    private Integer totalAmount;
    private Integer pointAmount;
    private OrderState orderState;

    private String orderPhone;
    private String orderEmail;

    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String customerRequest;

    public OrderRelatedInfoDto(OrderRelatedInfoWithAuth info){
        this.orderId = info.getOrderId();
        this.prefferedDeliveryDate = info.getPrefferedDeliveryDate();
        this.deliveryFee = info.getDeliveryFee();
        this.orderTime = info.getOrderTime();
        this.totalAmount = info.getTotalAmount();
        this.pointAmount = info.getPointAmount();
        this.orderState = info.getOrderState();

        this.orderPhone = info.getOrderPhone();
        this.orderEmail = info.getOrderEmail();

        this.receiverName = info.getReceiverName();
        this.receiverPhone = info.getReceiverPhone();
        this.receiverAddress = info.getReceiverAddress();
        this.customerRequest = info.getCustomerRequest();
    }

    public static OrderRelatedInfoDto makeFromOrder(Order order) {
        ShipmentInformation shipmentInformation = order.getShipmentInformation();

        return new OrderRelatedInfoDto(
                order.getId(),
                order.getPreferredDeliveryDate(),
                order.getDeliveryFee(),
                order.getOrderTime(),
                order.getTotalAmount(),
                order.getPointAmount(),
                order.getState(),

                order.getOrderPhone(),
                order.getOrderEmail(),

                shipmentInformation.getReceiverName(),
                shipmentInformation.getReceiverPhone(),
                shipmentInformation.getReceiverAddress(),
                shipmentInformation.getCustomerRequest());
    }
}

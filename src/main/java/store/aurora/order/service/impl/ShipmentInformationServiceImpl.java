package store.aurora.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.order.entity.ShipmentInformation;
import store.aurora.order.exception.exception404.OrderNotFoundException;
import store.aurora.order.exception.exception404.ShipmentInformationNotFoundException;
import store.aurora.order.exception.exception409.ShipmentInformationAlreadyExistsException;
import store.aurora.order.repository.ShipmentInformationRepository;
import store.aurora.order.service.OrderService;
import store.aurora.order.service.ShipmentInformationService;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ShipmentInformationServiceImpl implements ShipmentInformationService {

    private final ShipmentInformationRepository shipmentInformationRepository;
    private final OrderService orderService;

    private static final String ID_ARGUMENT_IS_NULL = "OrderId is null";

    @Override
    public boolean isExist(Long orderId) {
        if(Objects.isNull(orderId)){
            throw new IllegalArgumentException(ID_ARGUMENT_IS_NULL);
        }
        return shipmentInformationRepository.existsById(orderId);
    }

    @Override
    public void createShipmentInformation(ShipmentInformation shipmentInformation) {
        if(!validateShipmentInformation(shipmentInformation)){
            throw new ShipmentInformationAlreadyExistsException(shipmentInformation.getOrderId());
        }
//        if(!Objects.isNull(shipmentInformation.getOrder())){
//            shipmentInformation.setOrderId(shipmentInformation.getOrder().getId());
//        }
        shipmentInformationRepository.save(shipmentInformation);
    }

    @Override
    public ShipmentInformation getShipmentInformation(Long orderId) {
        if(Objects.isNull(orderId)){
            throw new IllegalArgumentException(ID_ARGUMENT_IS_NULL);
        }
        if(!orderService.isExist(orderId)){
            throw new OrderNotFoundException(orderId);
        }

        if(!isExist(orderId)){
            throw new ShipmentInformationNotFoundException(orderId);
        }

        return shipmentInformationRepository.getReferenceById(orderId);
    }

    @Override
    public void updateShipmentInformation(ShipmentInformation shipmentInformation) {
        if(Objects.isNull(shipmentInformation.getOrderId())){
            throw new IllegalArgumentException(ID_ARGUMENT_IS_NULL);
        }
        if(!orderService.isExist(shipmentInformation.getOrderId())){
            throw new OrderNotFoundException(shipmentInformation.getOrderId());
        }
        if(!isExist(shipmentInformation.getOrderId())){
            throw new ShipmentInformationNotFoundException(shipmentInformation.getOrderId());
        }
        validateShipmentInformation(shipmentInformation);
        shipmentInformationRepository.save(shipmentInformation);

    }

    @Override
    public void deleteShipmentInformationById(Long orderId) {
        if(Objects.isNull(orderId)){
            throw new IllegalArgumentException(ID_ARGUMENT_IS_NULL);
        }
        if(!orderService.isExist(orderId)){
            throw new OrderNotFoundException(orderId);
        }
        if(!isExist(orderId)){
            throw new ShipmentInformationNotFoundException(orderId);
        }
        shipmentInformationRepository.deleteById(orderId);
    }

    /**
     * ShipmentInformation 의 필수 속성이 null 인지 확인
     * Order에 이미 shipmentInformation이 존재하는지 확인
     * @param info ShipmentInformation
     * @return true: Order에 이미 shipmentInformation이 존재하는 경우, false: Order에 shipmentInformation이 존재하지 않는 경우
     */
    private boolean validateShipmentInformation(ShipmentInformation info) {
        if(Objects.isNull(info.getOrder())){
            throw new IllegalArgumentException("Order is null");
        }

        long orderId = info.getOrder().getId();
        if(!orderService.isExist(orderId)) {
            throw new OrderNotFoundException(orderId);
        }

        // 필수 속성이 null 인지 확인
        if(Objects.isNull(info.getReceiverName())
                || Objects.isNull(info.getReceiverPhone())
                || Objects.isNull(info.getReceiverAddress()) ){
            throw new IllegalArgumentException("Required Column null: ReceiverName, ReceiverPhone, ReceiverAddress");
        }

        // Order에 이미 shipmentInformation이 존재하는 경우
        return Objects.isNull(orderService.getOrder(orderId).getShipmentInformation());
    }
}
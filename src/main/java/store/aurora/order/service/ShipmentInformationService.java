package store.aurora.order.service;

import store.aurora.order.entity.ShipmentInformation;

import java.util.List;

public interface ShipmentInformationService {
    void createShipmentInformation(ShipmentInformation shipmentInformation);
    ShipmentInformation getShipmentInformation(Long orderId);
    void updateShipmentInformation(ShipmentInformation shipmentInformation);
    void deleteShipmentInformation(ShipmentInformation shipmentInformation);
}

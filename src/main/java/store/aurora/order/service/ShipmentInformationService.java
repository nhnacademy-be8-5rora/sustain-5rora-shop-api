package store.aurora.order.service;

import store.aurora.order.entity.ShipmentInformation;

public interface ShipmentInformationService {
    boolean isExist(Long orderId);
    void createShipmentInformation(ShipmentInformation shipmentInformation);
    ShipmentInformation getShipmentInformation(Long orderId);
    void updateShipmentInformation(ShipmentInformation shipmentInformation);
    void deleteShipmentInformationById(Long orderId);
}

package store.aurora.order.exception.exception409;

import store.aurora.common.exception.DataConflictException;

public class ShipmentInformationAlreadyExistsException extends DataConflictException {
    public ShipmentInformationAlreadyExistsException(String orderId) {
        super("Shipment Information already exists in order, order id: " + orderId);
    }
    public ShipmentInformationAlreadyExistsException(Long orderId) {
      super("Shipment Information already exists in order, order id: " + orderId);
    }
}

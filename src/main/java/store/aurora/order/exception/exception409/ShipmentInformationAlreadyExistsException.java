package store.aurora.order.exception.exception409;

import store.aurora.common.exception.DataAlreadyExistsException;

public class ShipmentInformationAlreadyExistsException extends DataAlreadyExistsException {
    public ShipmentInformationAlreadyExistsException(String orderId) {
        super("Shipment Information already exists in order, order id: " + orderId);
    }
    public ShipmentInformationAlreadyExistsException(Long orderId) {
      super("Shipment Information already exists in order, order id: " + orderId);
    }
}

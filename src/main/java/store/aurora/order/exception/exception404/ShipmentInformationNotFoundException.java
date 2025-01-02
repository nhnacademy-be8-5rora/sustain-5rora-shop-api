package store.aurora.order.exception.exception404;

import store.aurora.common.exception.DataNotFoundException;

public class ShipmentInformationNotFoundException extends DataNotFoundException {
    public ShipmentInformationNotFoundException(Long orderId) {
        super("ShipmentInformation Not Found Exception, OrderId: " + orderId);
    }
    public ShipmentInformationNotFoundException(String orderId) {
        super("ShipmentInformation Not Found Exception, Id: " + orderId);
    }
}

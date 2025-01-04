package store.aurora.order.exception.exception404;

import store.aurora.common.exception.DataNotFoundException;

public class ShipmentNotFoundException extends DataNotFoundException {
    public ShipmentNotFoundException(String shipmentId) {
        super("OrderDetail Not Found Exception, Id: " + shipmentId);
    }

    public ShipmentNotFoundException(Long shipmentId) {
        super("OrderDetail Not Found Exception, Id: " + shipmentId);
    }
}

package store.aurora.order.service;

import store.aurora.order.entity.Shipment;

import java.util.List;

/**
 * ShipmentService interface
 * Shipment (
 *  id: Long,
 *  trackingNumber: String | not null,
 *  shipmentCompaniesCode: ShippingCompaniesCode,
 *  shipmentDatetime: LocalDateTime,
 *  state: ShipmentState,
 *  orderDetails: List<OrderDetail>
 *     )
 */
public interface ShipmentService {
    Shipment createShipment(Shipment shipment);
    Shipment getShipment(Long id);
    List<Shipment> getShipments();
    void updateShipment(Shipment shipment);
    void deleteShipment(Shipment shipment);
    void deleteByShipmentId(Long shipmentId);
}

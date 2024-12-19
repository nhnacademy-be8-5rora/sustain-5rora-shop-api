package store.aurora.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.order.entity.Shipment;
import store.aurora.order.repository.ShipmentRepository;
import store.aurora.order.service.ShipmentService;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {
    private final ShipmentRepository shipmentRepository;
    @Override
    public Shipment createShipment(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }

    @Override
    public Shipment getShipment(Long id) {
        if(Objects.isNull(id)) {
            throw new IllegalArgumentException("id is null");
        }
        return shipmentRepository.getReferenceById(id);
    }

    @Override
    public List<Shipment> getShipments() {
        return shipmentRepository.findAll();
    }

    @Override
    public void updateShipment(Shipment shipment) {
        checkShipmentValuable(shipment);
        shipmentRepository.save(shipment);
    }

    @Override
    public void deleteShipment(Shipment shipment) {
        checkShipmentValuable(shipment);
        shipmentRepository.delete(shipment);
    }

    private void checkShipmentValuable(Shipment shipment) {
        if(Objects.isNull(shipment)) {
            throw new IllegalArgumentException("shipment is null");
        }
        if(Objects.isNull(shipment.getId())) {
            throw new IllegalArgumentException("shipment id is null");
        }
        if(!shipmentRepository.existsById(shipment.getId())) {
            throw new IllegalArgumentException("shipment is not exist");
        }
    }

    @Override
    public void deleteByShipmentId(Long shipmentId) {
        if(Objects.isNull(shipmentId)) {
            throw new IllegalArgumentException("shipmentId is null");
        }
        if(!shipmentRepository.existsById(shipmentId)) {
            throw new IllegalArgumentException("shipment is not exist");
        }
        shipmentRepository.deleteById(shipmentId);
    }
}

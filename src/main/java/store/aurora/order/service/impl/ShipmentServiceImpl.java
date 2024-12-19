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
    public boolean isExist(Long id) {
        if(Objects.isNull(id)) {
            throw new IllegalArgumentException("id is null");
        }
        return shipmentRepository.existsById(id);
    }

    @Override
    public Shipment createShipment(Shipment shipment) {
        if(Objects.isNull(shipment)) {
            throw new IllegalArgumentException("shipment is null");
        }
        if(Objects.isNull(shipment.getState())){
            throw new IllegalArgumentException("shipment state is null");
        }
        return shipmentRepository.save(shipment);
    }

    @Override
    public Shipment getShipment(Long id) {
        if(Objects.isNull(id)) {
            throw new IllegalArgumentException("id is null");
        }
        if(!shipmentRepository.existsById(id)) {
            throw new IllegalArgumentException("shipment is not exist");
        }
        return shipmentRepository.getReferenceById(id);
    }

    @Override
    // 필요한가?
    public List<Shipment> getShipments() {
        return shipmentRepository.findAll();
    }

    @Override
    public void updateShipment(Shipment shipment) {
        if(Objects.isNull(shipment)) {
            throw new IllegalArgumentException("shipment is null");
        }
        if(Objects.isNull(shipment.getState())){
            throw new IllegalArgumentException("shipment state is null");
        }
        if(!isExist(shipment.getId())) {
            throw new IllegalArgumentException("shipment is not exist");
        }
        shipmentRepository.save(shipment);
    }

    @Override
    public void deleteByShipmentId(Long shipmentId) {
        if(Objects.isNull(shipmentId)) {
            throw new IllegalArgumentException("shipmentId is null");
        }
        if(!isExist(shipmentId)) {
            throw new IllegalArgumentException("shipment is not exist");
        }
        shipmentRepository.deleteById(shipmentId);
    }
}

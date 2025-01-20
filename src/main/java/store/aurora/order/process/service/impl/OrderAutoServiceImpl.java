package store.aurora.order.process.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.order.process.service.OrderAutoService;
import store.aurora.order.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderAutoServiceImpl implements OrderAutoService {

    private static final Logger log = LoggerFactory.getLogger("user-logger");
    
    private final OrderRepository orderRepository;
    
    @Transactional
    @Override
    public int updateOrderAndOrderDetailsState(int daysThreshold) {
        int updated = orderRepository.updateOrderAndDetailsForExpiredShipments(daysThreshold);
        log.info("{} columns affected", updated);

        return updated;
    }
}

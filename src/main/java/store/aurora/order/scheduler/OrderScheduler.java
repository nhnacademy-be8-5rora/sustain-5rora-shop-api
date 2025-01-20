package store.aurora.order.scheduler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import store.aurora.order.process.service.OrderAutoService;

@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private static final Logger log = LoggerFactory.getLogger("user-logger");

    private final OrderAutoService orderAutoService;

    //@Scheduled(cron = "0 */5 * * * ?") //테스트용 5분마다 동작
    @Scheduled(cron = "0 0 0 * * ?") //매일 0시 0분 0초에 실행
    public void orderConfirm(){
        log.info("order scheduler started");
        int updated = orderAutoService.updateOrderAndOrderDetailsState(30);
        log.info("order scheduler completed. {} columns updated", updated);
    }
}

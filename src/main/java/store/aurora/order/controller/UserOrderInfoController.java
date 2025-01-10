package store.aurora.order.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import store.aurora.order.dto.OrderInfoDto;
import store.aurora.order.service.UserOrderInfoService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{user-id}/orders")
public class UserOrderInfoController {

    private static final Logger log = LoggerFactory.getLogger("user-logger");

    private final UserOrderInfoService userOrderInfoService;

    @GetMapping
    public Page<OrderInfoDto> getOrderInfoList(@PathVariable("user-id") String userId, Pageable pageable){
        return userOrderInfoService.getOrderInfos(userId, pageable);
    }
}

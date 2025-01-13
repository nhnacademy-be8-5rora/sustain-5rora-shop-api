package store.aurora.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.aurora.order.admin.dto.AdminOrderDTO;
import store.aurora.order.admin.service.AdminOrderService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    public Page<AdminOrderDTO> getAllOrderList(@RequestParam("page") int page,
                                               @RequestParam("size") int size){
        Pageable pageable = PageRequest.of(page, size);
        return adminOrderService.getAllOrderList(pageable);
    }

}

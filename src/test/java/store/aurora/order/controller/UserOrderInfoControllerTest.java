package store.aurora.order.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import store.aurora.common.encryptor.SimpleEncryptor;
import store.aurora.order.dto.OrderInfoDto;
import store.aurora.order.dto.OrderWithOrderDetailResponse;
import store.aurora.order.service.UserOrderInfoService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class UserOrderInfoControllerTest {

    private UserOrderInfoController userOrderInfoController;
    private UserOrderInfoService userOrderInfoService;
    private final SimpleEncryptor simpleEncryptor = new SimpleEncryptor();

    @BeforeEach
    void setUp() {
        userOrderInfoService = Mockito.mock(UserOrderInfoService.class);
        userOrderInfoController = new UserOrderInfoController(userOrderInfoService, simpleEncryptor);
    }

    @Test
    void getOrderInfoListTest() {
        PageImpl<OrderInfoDto> expected = new PageImpl<>(List.of());
        Mockito.when(userOrderInfoService.getOrderInfos(Mockito.anyString(), Mockito.any(Pageable.class))).thenReturn(expected);

        assertEquals(expected, userOrderInfoController.getOrderInfoList(simpleEncryptor.encrypt("text"), PageRequest.of(0, 10)));
    }

    @Test
    void getOrderWithOrderDetail() {
        OrderWithOrderDetailResponse expect = new OrderWithOrderDetailResponse(null, null);
        Mockito.when(userOrderInfoService.getOrderDetailInfos(Mockito.anyLong(), Mockito.anyString(), Mockito.eq(null))).thenReturn(expect);
        Long orderId = 1L;
        String userId = "test";
        String encryptedOrderId = simpleEncryptor.encrypt(String.valueOf(orderId));
        String encryptedUserId = simpleEncryptor.encrypt(userId);

        OrderWithOrderDetailResponse actual = userOrderInfoController.getOrderWithOrderDetail(encryptedUserId, encryptedOrderId);

        assertEquals(expect, actual);
    }
}
package store.aurora.order.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import store.aurora.common.encryptor.SimpleEncryptor;
import store.aurora.order.dto.OrderWithOrderDetailResponse;
import store.aurora.order.service.UserOrderInfoService;

import static org.junit.jupiter.api.Assertions.*;

class GenericUserOrderControllerTest {

    private GenericUserOrderController genericUserOrderController;
    private SimpleEncryptor simpleEncryptor;
    private UserOrderInfoService userOrderInfoService;

    @BeforeEach
    void setUp() {
        simpleEncryptor = new SimpleEncryptor();
        userOrderInfoService = Mockito.mock(UserOrderInfoService.class);
        genericUserOrderController = new GenericUserOrderController(simpleEncryptor, userOrderInfoService);
    }

    @Test
    void isOwnerTest1() {
        Mockito.when(userOrderInfoService.isOwner(Mockito.anyLong(), Mockito.anyString(), Mockito.eq(null))).thenReturn(true);

        String encryptedOrderId = simpleEncryptor.encrypt(String.valueOf(1L));
        String encryptedUserId = simpleEncryptor.encrypt("test");

        assertTrue(genericUserOrderController.isOwner(encryptedOrderId, encryptedUserId, null));

    }

    @Test
    void isOwnerTest2() {
        Mockito.when(userOrderInfoService.isOwner(Mockito.anyLong(), Mockito.eq(null), Mockito.any())).thenReturn(true);

        String encryptedOrderId = simpleEncryptor.encrypt(String.valueOf(1L));
        String encryptedPassword = simpleEncryptor.encrypt("1234");

        assertTrue(genericUserOrderController.isOwner(encryptedOrderId, null, encryptedPassword));
    }

    @Test
    void orderCancelTest() {
        String encryptedOrderId = simpleEncryptor.encrypt(String.valueOf(1L));
        Mockito.when(userOrderInfoService.cancelOrder(Mockito.anyLong())).thenReturn(1L);

        Long actual = genericUserOrderController.orderCancel(encryptedOrderId);

        Assertions.assertEquals(1L, actual);
    }

    @Test
    void requestRefundTest() {
        String encryptedOrderId = simpleEncryptor.encrypt(String.valueOf(1L));
        Mockito.when(userOrderInfoService.requestRefund(Mockito.anyLong())).thenReturn(1L);

        Long actual = genericUserOrderController.requestRefund(encryptedOrderId);

        Assertions.assertEquals(1L, actual);
    }

    @Test
    void getNonMemberOrderInfoTest1() {
        Long orderId = 1L;
        String password = "1234";
        String code = simpleEncryptor.encrypt(String.format("%s:%s", orderId, password));
        OrderWithOrderDetailResponse expect = new OrderWithOrderDetailResponse(null, null);
        Mockito.when(userOrderInfoService.getOrderDetailInfos(Mockito.anyLong(), Mockito.eq(null), Mockito.anyString())).thenReturn(expect);

        OrderWithOrderDetailResponse actual = genericUserOrderController.getNonMemberOrderInfo(code);

        Assertions.assertEquals(expect, actual);
    }

    @Test
    void getNonMemberOrderInfoTest2() {
        Long orderId = 1L;
        String password = "1234";
        String code = simpleEncryptor.encrypt(String.format("%s?%s", orderId, password));

        Assertions.assertThrows(IllegalArgumentException.class, () -> genericUserOrderController.getNonMemberOrderInfo(code));

    }
}
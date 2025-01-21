package store.aurora.order.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import store.aurora.book.entity.Book;
import store.aurora.coupon.feignclient.CouponClient;
import store.aurora.order.dto.OrderInfo;
import store.aurora.order.dto.OrderInfoDto;
import store.aurora.order.dto.OrderRelatedInfoWithAuth;
import store.aurora.order.dto.OrderWithOrderDetailResponse;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.OrderDetail;
import store.aurora.order.entity.Payment;
import store.aurora.order.entity.Shipment;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.exception.exception404.OrderNotFoundException;
import store.aurora.order.repository.OrderRepository;
import store.aurora.order.repository.PaymentRepository;
import store.aurora.order.service.impl.UserOrderInfoServiceImpl;
import store.aurora.point.repository.PointHistoryRepository;
import store.aurora.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserOrderInfoServiceTest {
    private UserOrderInfoService userOrderInfoService;
    private OrderRepository orderRepository;
    private PointHistoryRepository pointHistoryRepository;
    private PaymentRepository paymentRepository;
    private CouponClient couponClient;

    @BeforeEach
    void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        pointHistoryRepository = Mockito.mock(PointHistoryRepository.class);
        paymentRepository = Mockito.mock(PaymentRepository.class);
        couponClient = Mockito.mock(CouponClient.class);

        userOrderInfoService = new UserOrderInfoServiceImpl(orderRepository, pointHistoryRepository, paymentRepository, couponClient);
    }

    @Test
    void getOrderInfoTest() {
        PageImpl<OrderInfo> orderInfoPage = new PageImpl<>(List.of(new OrderInfo() {
            @Override
            public Long getOrderId() {
                return 1L;
            }

            @Override
            public Integer getTotalAmount() {
                return 1000;
            }

            @Override
            public Integer getOrderState() {
                return 0;
            }

            @Override
            public LocalDateTime getOrderTime() {
                return null;
            }

            @Override
            public String getOrderContent() {
                return "테스트";
            }
        }));
        Mockito.when(orderRepository.findOrderInfosByUserId(Mockito.anyString(), Mockito.any(Pageable.class))).thenReturn(orderInfoPage);

        Page<OrderInfoDto> actual = userOrderInfoService.getOrderInfos("test1", PageRequest.of(0, 10));

        assertEquals(1, actual.getSize());
    }

    @Test
    void getOrderDetailInfosTest() {
        OrderRelatedInfoWithAuth orderRelatedInfoWithAuth = new OrderRelatedInfoWithAuth();
        orderRelatedInfoWithAuth.setOrderId(1L);
        orderRelatedInfoWithAuth.setPassword("1234");

        Book book = new Book();
        book.setSalePrice(100);
        book.setTitle("text");

        Shipment shipment = new Shipment();
        shipment.setId(1L);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setBook(book);
        orderDetail.setShipment(shipment);

        Mockito.when(orderRepository.findOrderRelatedInfoByOrderId(1L)).thenReturn(Optional.of(orderRelatedInfoWithAuth));
        Mockito.when(orderRepository.findOrderDetailByOrderId(1L)).thenReturn(List.of(orderDetail));

        OrderWithOrderDetailResponse orderWithOrderDetailResponse = userOrderInfoService.getOrderDetailInfos(1L, null, "1234");

        Assertions.assertEquals(1, orderWithOrderDetailResponse.getOrderDetailInfoDtoList().size());
    }

    @Test
    void isOwnerTest1() {
        User user = new User();
        user.setId("test");

        Order order = new Order();
        order.setUser(user);

        Mockito.when(orderRepository.findOrderWithUserByOrderId(1L)).thenReturn(Optional.of(order));

        Assertions.assertTrue(userOrderInfoService.isOwner(1L, "test", null));
    }

    @Test
    void isOwnerTest2() {
        Order order = new Order();
        order.setPassword("1234");

        Mockito.when(orderRepository.findOrderWithUserByOrderId(1L)).thenReturn(Optional.of(order));

        Assertions.assertTrue(userOrderInfoService.isOwner(1L, null, "1234"));
    }

    @Test
    void isOwnerTest3() {
        Order order = new Order();
        Mockito.when(orderRepository.findOrderWithUserByOrderId(1L)).thenReturn(Optional.of(order));

        Assertions.assertFalse(userOrderInfoService.isOwner(1L, null, null));
    }

    @Test
    void cancelOrderTest() {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setShipment(new Shipment());

        Payment payment = new Payment();
        payment.setAmount(100);

        Order order = new Order();
        order.setPayments(List.of(payment));
        order.setOrderDetails(List.of(orderDetail));

        Mockito.when(orderRepository.findOrderByOrderIdWithShipmentInformationAndPaymentsAndUser(1L)).thenReturn(Optional.of(order));
        Mockito.when(paymentRepository.save(Mockito.any())).thenReturn(null);
        Mockito.when(pointHistoryRepository.save(Mockito.any())).thenReturn(null);

        Long actual = userOrderInfoService.cancelOrder(1L);

        Assertions.assertEquals(order.getId(), actual);
        Mockito.verify(orderRepository, Mockito.times(1)).findOrderByOrderIdWithShipmentInformationAndPaymentsAndUser(1L);
        Mockito.verify(paymentRepository,Mockito.times(1)).save(Mockito.any());
        Mockito.verify(pointHistoryRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void requestRefundTest1() {
        List<OrderDetail> orderDetails = List.of(new OrderDetail());
        Order order = new Order();
        order.setOrderDetails(orderDetails);
        order.setId(1L);

        Mockito.when(orderRepository.findOrderByOrderIdWithShipmentInformationAndPaymentsAndUser(1L)).thenReturn(Optional.of(order));

        Long actual = userOrderInfoService.requestRefund(1L);

        Assertions.assertEquals(1L, actual);
        Mockito.verify(orderRepository, Mockito.times(1)).findOrderByOrderIdWithShipmentInformationAndPaymentsAndUser(1L);
    }

    @Test
    void requestRefundTest2() {
        Mockito.when(orderRepository.findOrderByOrderIdWithShipmentInformationAndPaymentsAndUser(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> userOrderInfoService.requestRefund(1L));
    }

    @Test
    void resolveRefundTest() {
        Order order = new Order();
        order.setOrderDetails(List.of());
        order.setId(1L);
        Payment payment = new Payment();
        payment.setAmount(100);
        order.setPayments(List.of(payment));

        Mockito.when(orderRepository.findOrderDetailByOrderId(Mockito.anyLong())).thenReturn(List.of());
        Mockito.when(orderRepository.findOrderByOrderIdWithShipmentInformationAndPaymentsAndUser(1L)).thenReturn(Optional.of(order));
        Mockito.when(paymentRepository.save(Mockito.any())).thenReturn(null);
        Mockito.when(pointHistoryRepository.save(Mockito.any())).thenReturn(null);
        Mockito.when(couponClient.refund(Mockito.anyList())).thenReturn(null);

        Long actual = userOrderInfoService.resolveRefund(1L);

        Assertions.assertEquals(order.getId(), actual);
    }

    @Test
    void getOrderInfoByState() {
        Mockito.when(orderRepository.findOrderInfosByOrderState(Mockito.anyInt(), Mockito.any(Pageable.class))).thenReturn(Page.empty());

        Page<OrderInfoDto> orderInfosByState = userOrderInfoService.getOrderInfosByState(OrderState.PENDING, PageRequest.of(0, 10));

        Assertions.assertEquals(0, orderInfosByState.getSize());
    }
}
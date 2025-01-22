package store.aurora.order.process.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.aurora.book.entity.Book;
import store.aurora.book.service.book.BookService;
import store.aurora.order.dto.OrderDetailDTO;
import store.aurora.order.dto.OrderRequestDto;
import store.aurora.order.dto.OrderResponseDto;
import store.aurora.order.entity.*;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.entity.enums.ShipmentState;
import store.aurora.order.process.service.DeliveryFeeService;
import store.aurora.order.process.service.OrderInfoService;
import store.aurora.order.process.service.TotalAmountGetter;
import store.aurora.order.service.*;
import store.aurora.point.service.PointSpendService;
import store.aurora.user.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderProcessServiceImplTest {

    @Mock private BookService bookService;
    @Mock private OrderService orderService;
    @Mock private OrderInfoService orderInfoService;
    @Mock private UserService userService;
    @Mock private ShipmentService shipmentService;
    @Mock private WrapService wrapService;
    @Mock private OrderDetailService orderDetailService;
    @Mock private ShipmentInformationService shipmentInformationService;
    @Mock private PaymentService paymentService;
    @Mock private PointSpendService pointSpendService;
    @Mock private DeliveryFeeService deliveryFeeService;
    @Mock private TotalAmountGetter totalAmountGetter;

    @InjectMocks
    private OrderProcessServiceImpl orderProcessService;

    private String redisOrderId;
    private OrderRequestDto mockOrderRequest;
    private Order mockOrder;
    private Book mockBook;

    @BeforeEach
    void setUp() {
        redisOrderId = UUID.randomUUID().toString();

        // 주문 요청 DTO 생성
        mockOrderRequest = new OrderRequestDto(
                "user123", "password123", // 인증 정보
                "홍길동", "010-1234-5678", "hong@example.com", LocalDate.of(2025, 1, 25), // 주문자 정보
                "이순신", "010-9876-5432", "lee@example.com", "서울특별시 강남구", "빠른 배송 부탁드립니다.", // 받는 사람 정보
                List.of(new OrderDetailDTO(1L, 2, 101L, 201L, 3000)), // 상품 정보
                500  // 사용한 포인트
        );

        // 모의 Order 엔티티 생성
        mockOrder = Order.builder()
                .id(1L)
                .name(mockOrderRequest.getOrdererName())
                .orderTime(LocalDateTime.now())
                .totalAmount(50000)
                .pointAmount(500)
                .state(OrderState.PENDING)
                .build();

        mockBook = new Book();
        mockBook.setSalePrice(10000);

    }

    @Test
    void getOrderResponseFromOrderRequestDtoInRedis() {
        // given
        when(orderInfoService.getOrderInfoFromRedis(redisOrderId)).thenReturn(mockOrderRequest);
        when(totalAmountGetter.getTotalAmountFromOrderDetailList(mockOrderRequest.getOrderDetailDTOList()))
                .thenReturn(55000); // 총 금액 55000원

        // when
        OrderResponseDto response = orderProcessService.getOrderResponseFromOrderRequestDtoInRedis(redisOrderId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCurrency()).isEqualTo("KRW");
        assertThat(response.getValue()).isEqualTo(54500); // 55000 - 500 포인트 차감
        assertThat(response.getOrderName()).isNotBlank();

        verify(orderInfoService, times(1)).getOrderInfoFromRedis(redisOrderId);
    }

    @Test
    void userOrderProcess() {
        // given
        when(orderInfoService.getOrderInfoFromRedis(redisOrderId)).thenReturn(mockOrderRequest);
        when(deliveryFeeService.getDeliveryFee(50000)).thenReturn(3000);
        when(userService.getUser("user123")).thenReturn(null);
        when(orderService.createOrder(any(Order.class))).thenReturn(mockOrder);
        when(shipmentService.createShipment(any(Shipment.class))).thenReturn(Shipment.builder().state(ShipmentState.PENDING).build());
        when(wrapService.getWrap(anyLong())).thenReturn(new Wrap(1L, 1000, "포장1", null));
        when(bookService.getBookById(anyLong())).thenReturn(mockBook);
        doNothing().when(orderDetailService).createOrderDetail(any(OrderDetail.class));
        doNothing().when(shipmentInformationService).createShipmentInformation(any(ShipmentInformation.class));

        // when
        Order result = orderProcessService.userOrderProcess(redisOrderId, "payment-key-123", 50000);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getState()).isEqualTo(OrderState.PENDING);
        assertThat(result.getTotalAmount()).isEqualTo(50000);

        verify(orderService, times(1)).createOrder(any(Order.class));
    }

    @Test
    void nonUserOrderProcess() {
        // given
        when(orderInfoService.getOrderInfoFromRedis(redisOrderId)).thenReturn(mockOrderRequest);
        when(deliveryFeeService.getDeliveryFee(50000)).thenReturn(3000);
        when(orderService.createOrder(any(Order.class))).thenReturn(mockOrder);
        when(shipmentService.createShipment(any(Shipment.class))).thenReturn(Shipment.builder().state(ShipmentState.PENDING).build());
        when(wrapService.getWrap(anyLong())).thenReturn(new Wrap(1L, 1000, "포장1", null));
        when(bookService.getBookById(anyLong())).thenReturn(mockBook);
        doNothing().when(orderDetailService).createOrderDetail(any(OrderDetail.class));
        doNothing().when(shipmentInformationService).createShipmentInformation(any(ShipmentInformation.class));

        // when
        Long orderId = orderProcessService.nonUserOrderProcess(redisOrderId, "payment-key-123", 50000);

        // then
        assertThat(orderId).isEqualTo(mockOrder.getId());

        verify(orderService, times(1)).createOrder(any(Order.class));
    }

    @Test
    void saveOrderInfoInRedisWithUuid() {
        // when
        orderProcessService.saveOrderInfoInRedisWithUuid(redisOrderId, mockOrderRequest);

        // then
        verify(orderInfoService, times(1)).saveOrderInfoInRedisWithUuid(redisOrderId, mockOrderRequest);
    }

    @Test
    void getOrderUuid() {
        // when
        String uuid = orderProcessService.getOrderUuid();

        // then
        assertThat(uuid).isNotBlank();
    }
}

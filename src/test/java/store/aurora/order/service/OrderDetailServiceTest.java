package store.aurora.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import store.aurora.book.entity.Book;
import store.aurora.book.service.BookService;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.OrderDetail;
import store.aurora.order.entity.Shipment;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.entity.enums.ShipmentState;
import store.aurora.order.exception.exception404.OrderDetailNotFoundException;
import store.aurora.order.exception.exception404.OrderNotFoundException;
import store.aurora.order.repository.OrderDetailRepository;
import store.aurora.order.service.impl.OrderDetailServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderDetailServiceTest {

    private OrderDetailRepository orderDetailRepository;
    private OrderService orderService;
    private BookService bookService;
    private ShipmentService shipmentService;

    private OrderDetailService orderDetailService;

    @BeforeEach
    void setUp() {
        orderService = Mockito.mock(OrderService.class);
        orderDetailRepository = Mockito.mock(OrderDetailRepository.class);
        shipmentService = Mockito.mock(ShipmentService.class);
        bookService = Mockito.mock(BookService.class);

        orderDetailService = new OrderDetailServiceImpl(orderService, orderDetailRepository, shipmentService, bookService);

        Order order = new Order();
        order.setId(1L);
        order.setDeliveryFee(0);
        order.setOrderTime(LocalDateTime.now());
        order.setTotalAmount(0);
        order.setPointAmount(0);
        order.setState(OrderState.PENDING);
        order.setName("name");
        order.setOrderPhone("");
        order.setOrderEmail("");
        order.setPassword("");
        when(orderService.getOrder(1L)).thenReturn(order);
        when(orderService.isExist(order.getId())).thenReturn(true);


        Book book = new Book();
        book.setId(1L);
        book.setTitle("title");
        book.setRegularPrice(10000);
        book.setSalePrice(10000);
        book.setSale(true);
        book.setIsbn("");
        book.setExplanation("");
        book.setPublishDate(LocalDate.now());
        when(bookService.getBookById(anyLong())).thenReturn(book);
        doNothing().when(bookService).notExistThrow(anyLong());

        Shipment shipment = new Shipment();
        shipment.setId(1L);
        shipment.setState(ShipmentState.PENDING);
        when(shipmentService.getShipment(anyLong())).thenReturn(shipment);
        when(shipmentService.isExist(anyLong())).thenReturn(true);
    }

    @Test
    void isExist() {
        when(orderDetailRepository.existsById(1L)).thenReturn(true);
        assertTrue(orderDetailService.isExist(1L));

        when(orderDetailRepository.existsById(2L)).thenReturn(false);
        assertFalse(orderDetailService.isExist(2L));
    }

    @Test
    void isExistWithNull(){
        assertThrows(IllegalArgumentException.class, () -> orderDetailService.isExist(null));
    }

    @Test
    void createOrderDetail() {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(orderService.getOrder(1L));
        orderDetail.setBook(bookService.getBookById(1L));
        orderDetail.setQuantity(1);
        orderDetail.setShipment(shipmentService.getShipment(1L));
        orderDetail.setAmountDetail(10000);

        orderDetailService.createOrderDetail(orderDetail);

        verify(orderDetailRepository, times(1)).save(orderDetail);
    }

    @Test
    void createOrderDetailWithValidateAndThrows(){
        assertAll(
                // orderDetail is null
                ()->{
                    assertThrows(IllegalArgumentException.class,
                            ()->orderDetailService.createOrderDetail(null));
                },

                // order is null
                ()->{
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setBook(bookService.getBookById(1L));
                    orderDetail.setQuantity(1);
                    orderDetail.setShipment(shipmentService.getShipment(1L));
                    orderDetail.setAmountDetail(10000);
                    assertThrows(IllegalArgumentException.class,
                            ()->orderDetailService.createOrderDetail(orderDetail));
                },

                // book is null
                ()->{
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(orderService.getOrder(1L));
                    orderDetail.setQuantity(1);
                    orderDetail.setShipment(shipmentService.getShipment(1L));
                    orderDetail.setAmountDetail(10000);
                    assertThrows(IllegalArgumentException.class,
                            ()->orderDetailService.createOrderDetail(orderDetail));
                },

                // quantity is null
                ()->{
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(orderService.getOrder(1L));
                    orderDetail.setBook(bookService.getBookById(1L));
                    orderDetail.setShipment(shipmentService.getShipment(1L));
                    orderDetail.setAmountDetail(10000);
                    assertThrows(IllegalArgumentException.class,
                            ()->orderDetailService.createOrderDetail(orderDetail));
                },

                // shipment is null
                ()->{
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(orderService.getOrder(1L));
                    orderDetail.setBook(bookService.getBookById(1L));
                    orderDetail.setQuantity(1);
                    orderDetail.setAmountDetail(10000);
                    assertThrows(IllegalArgumentException.class,
                            ()->orderDetailService.createOrderDetail(orderDetail));
                },

                // amountDetail is null
                ()->{
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(orderService.getOrder(1L));
                    orderDetail.setBook(bookService.getBookById(1L));
                    orderDetail.setQuantity(1);
                    orderDetail.setShipment(shipmentService.getShipment(1L));
                    assertThrows(IllegalArgumentException.class,
                            ()->orderDetailService.createOrderDetail(orderDetail));
                },

                // book does not exist
                ()->{
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(orderService.getOrder(1L));
                    orderDetail.setBook(bookService.getBookById(1L));
                    orderDetail.setQuantity(1);
                    orderDetail.setShipment(shipmentService.getShipment(1L));
                    orderDetail.setAmountDetail(10000);
                    doThrow(IllegalArgumentException.class).when(bookService).notExistThrow(anyLong());
                    assertThrows(IllegalArgumentException.class,
                            ()->orderDetailService.createOrderDetail(orderDetail));
                },

                // shipment does not exist
                ()->{
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(orderService.getOrder(1L));
                    orderDetail.setBook(bookService.getBookById(1L));
                    orderDetail.setQuantity(1);
                    orderDetail.setShipment(shipmentService.getShipment(1L));
                    orderDetail.setAmountDetail(10000);
                    orderDetail.setShipment(shipmentService.getShipment(1L));
                    when(shipmentService.isExist(anyLong())).thenReturn(false);
                    assertThrows(IllegalArgumentException.class,
                            ()->orderDetailService.createOrderDetail(orderDetail));
                },

                // order does not exist
                ()->{
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(orderService.getOrder(1L));
                    orderDetail.setBook(bookService.getBookById(1L));
                    orderDetail.setQuantity(1);
                    orderDetail.setShipment(shipmentService.getShipment(1L));
                    orderDetail.setAmountDetail(10000);
                    when(orderService.isExist(anyLong())).thenReturn(false);
                    assertThrows(OrderNotFoundException.class,
                            ()->orderDetailService.createOrderDetail(orderDetail));
                }
        );
    }

    @Test
    void getOrderDetail() {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setId(1L);
        orderDetail.setOrder(orderService.getOrder(1L));
        orderDetail.setBook(bookService.getBookById(1L));
        orderDetail.setQuantity(1);
        orderDetail.setShipment(shipmentService.getShipment(1L));
        orderDetail.setAmountDetail(10000);

        when(orderDetailRepository.getReferenceById(1L)).thenReturn(orderDetail);
        when(orderDetailRepository.existsById(1L)).thenReturn(true);

        assertEquals(orderDetail, orderDetailService.getOrderDetail(1L));
    }

    @Test
    void getOrderDetailWithThrows(){
        assertAll(
                // orderDetailId is null
                ()->{
                    assertThrows(IllegalArgumentException.class,
                            ()->orderDetailService.getOrderDetail(null));
                },

                // orderDetail does not exist
                ()->{
                    when(orderDetailRepository.existsById(anyLong())).thenReturn(false);
                    assertThrows(OrderDetailNotFoundException.class,
                            ()->orderDetailService.getOrderDetail(1L));
                }
        );
    }

    @Test
    void getOrderDetails() {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setId(1L);
        orderDetail.setOrder(orderService.getOrder(1L));
        orderDetail.setBook(bookService.getBookById(1L));
        orderDetail.setQuantity(1);
        orderDetail.setShipment(shipmentService.getShipment(1L));
        orderDetail.setAmountDetail(10000);

        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setId(2L);
        orderDetail1.setOrder(orderService.getOrder(1L));
        orderDetail1.setBook(bookService.getBookById(1L));
        orderDetail1.setQuantity(1);
        orderDetail1.setShipment(shipmentService.getShipment(1L));
        orderDetail1.setAmountDetail(10000);

        when(orderDetailRepository.findAll()).thenReturn(List.of(orderDetail, orderDetail1));

        List<OrderDetail> details = orderDetailService.getOrderDetails();
        assertEquals(2, details.size());
        assertTrue(details.contains(orderDetail));
        assertTrue(details.contains(orderDetail1));

        verify(orderDetailRepository, times(1)).findAll();
    }

    @Test
    void getOrderDetailsByOrder() {
        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setId(1L);
        orderDetail1.setOrder(orderService.getOrder(1L));
        orderDetail1.setBook(bookService.getBookById(1L));
        orderDetail1.setQuantity(1);
        orderDetail1.setShipment(shipmentService.getShipment(1L));
        orderDetail1.setAmountDetail(10000);

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.setId(2L);
        orderDetail2.setOrder(orderService.getOrder(1L));
        orderDetail2.setBook(bookService.getBookById(1L));
        orderDetail2.setQuantity(1);
        orderDetail2.setShipment(shipmentService.getShipment(1L));
        orderDetail2.setAmountDetail(10000);

        when(orderDetailRepository.findByOrder(orderService.getOrder(1L))).thenReturn(List.of(orderDetail1, orderDetail2));

        List<OrderDetail> details = orderDetailService.getOrderDetailsByOrder(orderService.getOrder(1L));
        assertEquals(2, details.size());
        assertTrue(details.contains(orderDetail1));
        assertTrue(details.contains(orderDetail2));

        verify(orderDetailRepository, times(1)).findByOrder(orderService.getOrder(1L));
    }

    @Test
    void getOrderDetailsByOrderWithThrows(){
        assertAll(
                // order is null
                ()->{
                    assertThrows(IllegalArgumentException.class,
                            ()->orderDetailService.getOrderDetailsByOrder(null));
                },

                // order does not exist
                ()->{
                    when(orderService.isExist(anyLong())).thenReturn(false);
                    assertThrows(OrderNotFoundException.class,
                            ()->orderDetailService.getOrderDetailsByOrder(orderService.getOrder(1L)));
                }
        );
    }

    @Test
    void updateOrderDetail() {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setId(1L);
        orderDetail.setOrder(orderService.getOrder(1L));
        orderDetail.setBook(bookService.getBookById(1L));
        orderDetail.setQuantity(1);
        orderDetail.setShipment(shipmentService.getShipment(1L));
        orderDetail.setAmountDetail(10000);

        when(orderDetailRepository.existsById(1L)).thenReturn(true);

        orderDetailService.updateOrderDetail(orderDetail);

        verify(orderDetailRepository, times(1)).save(orderDetail);
    }

    @Test
    void updateOrderDetailWithThrows(){
        assertAll(
                // orderDetail is null
                ()->{
                    assertThrows(IllegalArgumentException.class,
                            ()->orderDetailService.updateOrderDetail(null));
                },

                // orderDetail does not exist
                ()->{
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setId(1L);
                    orderDetail.setOrder(orderService.getOrder(1L));
                    orderDetail.setBook(bookService.getBookById(1L));
                    orderDetail.setQuantity(1);
                    orderDetail.setShipment(shipmentService.getShipment(1L));
                    orderDetail.setAmountDetail(10000);
                    when(orderDetailRepository.existsById(anyLong())).thenReturn(false);
                    assertThrows(OrderDetailNotFoundException.class,
                            ()->orderDetailService.updateOrderDetail(orderDetail));
                }
        );
    }

    @Test
    void deleteOrderDetailById() {
        when(orderDetailRepository.existsById(1L)).thenReturn(true);

        orderDetailService.deleteOrderDetailById(1L);

        verify(orderDetailRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteOrderDetailByIdWithThrows(){
        assertAll(
                // orderDetailId is null
                ()->{
                    assertThrows(IllegalArgumentException.class,
                            ()->orderDetailService.deleteOrderDetailById(null));
                },

                // orderDetail does not exist
                ()->{
                    when(orderDetailRepository.existsById(anyLong())).thenReturn(false);
                    assertThrows(OrderDetailNotFoundException.class,
                            ()->orderDetailService.deleteOrderDetailById(1L));
                }
        );
    }
}

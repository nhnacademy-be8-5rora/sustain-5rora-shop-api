package store.aurora.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.Publisher;
import store.aurora.book.repository.PublisherRepository;
import store.aurora.book.service.BookService;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.OrderDetail;
import store.aurora.order.entity.Shipment;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.entity.enums.ShipmentState;
import store.aurora.order.repository.OrderDetailRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderDetailServiceImplTest {
    @MockBean
    private OrderDetailRepository orderDetailRepository;
    @MockBean
    private OrderService orderService;
    @MockBean
    private BookService bookService;
    @MockBean
    private PublisherRepository publisherRepository;
    @MockBean
    private ShipmentService shipmentService;

    @Autowired
    private OrderDetailServiceImpl orderDetailService;

    @BeforeEach
    void setUp() {
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

        Publisher publisher = new Publisher(1L, "");
        when(publisherRepository.findById(1L)).thenReturn(java.util.Optional.of(publisher));

        Book book = new Book();
        book.setId(1L);
        book.setTitle("title");
        book.setRegularPrice(10000);
        book.setSalePrice(10000);
        book.setSale(true);
        book.setIsbn("");
        book.setExplanation("");
        book.setPublishDate(LocalDate.now());
        book.setPublisher(publisher);
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
                    assertThrows(IllegalArgumentException.class,
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
                    assertThrows(IllegalArgumentException.class,
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

        when(orderDetailRepository.findAll()).thenReturn(java.util.List.of(orderDetail, orderDetail1));

        List<OrderDetail> details = orderDetailService.getOrderDetails();
        assertEquals(2, details.size());
        assertTrue(details.contains(orderDetail));
        assertTrue(details.contains(orderDetail1));

        verify(orderDetailRepository, times(1)).findAll();
    }

    @Test
    void getOrderDetailsByOrder() {
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

        when(orderDetailRepository.findByOrder(orderService.getOrder(1L))).thenReturn(java.util.List.of(orderDetail, orderDetail1));

        List<OrderDetail> details = orderDetailService.getOrderDetailsByOrder(orderService.getOrder(1L));
        assertEquals(2, details.size());
        assertTrue(details.contains(orderDetail));
        assertTrue(details.contains(orderDetail1));

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
                    assertThrows(IllegalArgumentException.class,
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
                    assertThrows(IllegalArgumentException.class,
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
                    assertThrows(IllegalArgumentException.class,
                            ()->orderDetailService.deleteOrderDetailById(1L));
                }
        );
    }
}
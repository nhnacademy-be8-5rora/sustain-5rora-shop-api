package store.aurora.order.process.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.aurora.book.entity.Book;
import store.aurora.book.service.book.BookService;
import store.aurora.order.dto.OrderDetailDTO;
import store.aurora.order.entity.Wrap;
import store.aurora.order.process.service.DeliveryFeeService;
import store.aurora.order.service.WrapService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TotalAmountGetterImplTest {
    @Mock
    BookService bookService;

    @Mock
    WrapService wrapService;

    @Mock
    DeliveryFeeService deliveryFeeService;

    @InjectMocks
    TotalAmountGetterImpl totalAmountGetter;

    @Test
    void getTotalAmountFromOrderDetailList() {

        // given
        Book mockBook = new Book();
        mockBook.setSalePrice(1000);

        when(bookService.getBookById(anyLong())).thenReturn(mockBook);

        Wrap mockWrap = new Wrap();
        mockWrap.setAmount(1000);
        when(wrapService.getWrap(anyLong())).thenReturn(mockWrap);

        when(deliveryFeeService.getDeliveryFee(anyInt())).thenReturn(3000);


        List<OrderDetailDTO> orderDetailList = List.of(
                new OrderDetailDTO(1L, 1, 1L, 1L, 1000),
                new OrderDetailDTO(2L, 2, 2L, 2L, 1000),
                new OrderDetailDTO(3L, 3, 3L, 3L, 1000)
        );

        // when | then
        int totalAmount = totalAmountGetter.getTotalAmountFromOrderDetailList(orderDetailList);

        /*
        1번 책: 1000 * 1 + 1000 * 1 = 2000
        2번 책: 1000 * 2 + 1000 * 2 = 4000
        3번 책: 1000 * 3 + 1000 * 3 = 6000
        배송비 : 3000
        총합 : 2000 + 4000 + 6000 + 3000 = 15000
         */
        assertThat(totalAmount).isEqualTo(12000);
    }

    @Test
    void getTotalAmountFromOrderDetailListWhenWrapAndDiscountAmountIsNull() {
        // given
        Book mockBook = new Book();
        mockBook.setSalePrice(1000);

        when(bookService.getBookById(anyLong())).thenReturn(mockBook);

        when(deliveryFeeService.getDeliveryFee(anyInt())).thenReturn(3000);

        List<OrderDetailDTO> orderDetailList = List.of(
                new OrderDetailDTO(1L, 1, null, null, null),
                new OrderDetailDTO(2L, 2, null, null, null),
                new OrderDetailDTO(3L, 3, null, null, null)
        );

        // when | then
        int totalAmount = totalAmountGetter.getTotalAmountFromOrderDetailList(orderDetailList);

        assertThat(totalAmount).isEqualTo(9000);
    }
}
package store.aurora.order.process.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.service.book.BookService;
import store.aurora.order.dto.OrderDetailDTO;
import store.aurora.order.process.service.DeliveryFeeService;
import store.aurora.order.service.WrapService;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TotalAmountGetterImpl implements store.aurora.order.process.service.TotalAmountGetter {

    private final BookService bookService;
    private final WrapService wrapService;
    private final DeliveryFeeService deliveryFeeService;

    @Override
    @Transactional(readOnly = true)
    public int getTotalAmountFromOrderDetailList(List<OrderDetailDTO> orderDetailList) {
        int totalAmount = 0;

        for (OrderDetailDTO detail : orderDetailList) {
            // 책 가격
            // 책이 없는 경우는 bookService에서 에러 처리 되어 있음
            int bookSalePrice = bookService.getBookById(detail.getBookId()).getSalePrice();

            // 책 가격 계산
            int amount = bookSalePrice * detail.getQuantity();

            // wrap 금액 적용
            if(Objects.nonNull(detail.getWrapId()))
                amount += wrapService.getWrap(detail.getWrapId()).getAmount()
                        * detail.getQuantity();

            // 할인 금액 적용
            if(Objects.nonNull(detail.getDiscountAmount()))
                amount -= detail.getDiscountAmount();

            totalAmount += amount;
        }

        // 배송비 계산
        totalAmount += deliveryFeeService.getDeliveryFee(totalAmount);

        return totalAmount;
    }
}

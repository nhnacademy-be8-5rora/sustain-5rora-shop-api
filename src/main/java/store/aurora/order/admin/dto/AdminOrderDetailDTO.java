package store.aurora.order.admin.dto;

import lombok.*;

@Getter
@Builder
public class AdminOrderDetailDTO{
    private String bookName;
    private Integer price;
    private Integer quantity;
}


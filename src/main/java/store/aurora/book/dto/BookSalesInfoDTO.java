package store.aurora.book.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSalesInfoDTO {
    private boolean isSale;
    private int salePrice;
    private int stock;
    private boolean packaging;
}

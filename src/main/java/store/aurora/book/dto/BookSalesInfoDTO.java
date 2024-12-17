package store.aurora.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookSalesInfoDTO {
    private Boolean isSale;
    private Integer salePrice;
    private Integer stock;
    private Boolean packaging;
}
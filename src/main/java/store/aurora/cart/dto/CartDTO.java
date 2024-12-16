package store.aurora.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CartDTO {
    private Long bookId;
    private Integer quantity;

    private String title;
    private int regularPrice;
    private int salePrice;
    private Integer stock;
    private boolean isSale;
    private String filePath;

    public CartDTO(Long bookId, Integer quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }
}

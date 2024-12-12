package store.aurora.entity.cart;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart_items")
@NoArgsConstructor
@Getter
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity = 1;

    @Setter
    @NotNull
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

//    @Setter
//    @NotNull
//    @OneToOne//(fetch = FetchType.LAZY)
//    @JoinColumn(name = "book_id", nullable = false)
//    private Book book;
    private Long BookId;

//    public CartItem(Cart cart, Book book) {
//        this.cart = cart;
//        this.book = book;
//    }

    public void setQuantity(Integer quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        this.quantity = quantity;
    }
}

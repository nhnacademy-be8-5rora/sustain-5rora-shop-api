package store.aurora.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wraps")
public class Wrap {
    @Id @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 가격(원) default 0
    @NotNull
    @Column(name="amount")
    private int amount;

    // 포장 종류 이름
    @NotNull
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "wraps")
    private List<OrderDetail> orderDetails;

    public Wrap(int amount, String name) {
        this.amount = amount;
        this.name = name;
    }
}

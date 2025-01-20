package store.aurora.order.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "wraps")
public class Wrap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 가격(원) default 0
    @ColumnDefault("0")
    @Column(name="amount", nullable = false)
    private Integer amount;

    // 포장 종류 이름
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @OneToMany(mappedBy = "wrap", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    public void addOrderDetail(OrderDetail orderDetail) {
        if (orderDetails == null) {
            orderDetails = new ArrayList<>();
        }
        orderDetails.add(orderDetail);
        orderDetail.setWrap(this); // 양방향 관계 동기화
    }
}

package store.aurora.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shipment_information")
public class ShipmentInformation {
    @Id
    private Long orderId;

    @OneToOne
    @MapsId // orderId를 외래키로 사용
    @JoinColumn(name = "order_id")
    private Order order;

    // 수취인 이름
    @NotNull
    @Column(name = "receiver_name")
    private String receiverName;

    // 수취인 주소
    @NotNull
    @Column(name = "receiver_address")
    private String receiverAddress;

    // 수취인 전화번호
    @NotNull
    @Column(name = "receiver_phone")
    private String receiverPhone;

    // 배송 요청사항
    @Column(name = "customer_request")
    private String customerRequest;
}

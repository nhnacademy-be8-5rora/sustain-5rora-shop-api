package store.aurora.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import store.aurora.order.entity.enums.ShipmentState;
import store.aurora.order.entity.enums.ShippingCompaniesCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shipments")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 운송장 번호, 대기 상태일 때 null
    @Column(name = "tracking_number")
    private String trackingNumber;

    // 택배사 코드
    @NotNull
    @Column(name = "shipment_companies_code")
    private ShippingCompaniesCode shipmentCompaniesCode;

    // 출고일
    @Column(name = "shipment_datetime")
    private LocalDateTime shipmentDatetime;

    // 배송 상태
    @NotNull
    @Column(name = "state")
    private ShipmentState state;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    // Shipment 클래스에 편의 메서드 추가
    public void addOrderDetail(OrderDetail orderDetail) {
        if (orderDetails == null) {
            orderDetails = new ArrayList<>();
        }
        orderDetails.add(orderDetail);
        orderDetail.setShipment(this); // 양방향 관계 동기화
    }
}

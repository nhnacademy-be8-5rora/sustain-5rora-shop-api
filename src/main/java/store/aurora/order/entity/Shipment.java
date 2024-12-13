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
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shipments")
public class Shipment {
    @Id @NotNull
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

    @OneToMany(mappedBy = "shipments")
    private List<OrderDetail> orderDetails;
}

package store.aurora.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_addresses")
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    @NotNull
    @Column(name = "user_addr_receiver", length = 30, nullable = false)
    private String receiver;

    @Setter
    @NotNull
    @Column(name = "user_addr_detail", nullable = false)
    private String addrDetail;

    // 유저 엔터티와 다대일 연결
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 주소 엔터티와 다대일 연결
    @Setter
    @NotNull
    @ManyToOne
    @JoinColumn(name = "addr_id", nullable = false)
    private Address address;

    public UserAddress(String addrDetail, Address address, String receiver, User user) {
        this.addrDetail = addrDetail;
        this.address = address;
        this.receiver = receiver;
        this.user = user;
    }
}
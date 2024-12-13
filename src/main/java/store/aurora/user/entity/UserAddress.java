package store.aurora.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_addresses")
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_addr_nickname", length = 50)
    private String nickname;

    @Column(name = "user_addr_detail", length = 255)
    private String addrDetail;


    // 유저 엔터티와 다대일 연결
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 주소 엔터티와 다대일 연결
    @ManyToOne
    @JoinColumn(name = "addr_id", nullable = false)
    private Address address;

}

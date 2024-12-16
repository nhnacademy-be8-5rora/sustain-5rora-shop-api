package store.aurora.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import store.aurora.order.entity.Order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;

    @Column(name = "user_pwd")
    private String password;

    @Column(name = "user_name", nullable = false, length = 50)
    private String name;

    @Column(name = "user_birthday")
    private LocalDate birth;

    @Column(name = "user_phone_number", nullable = false, length = 13)
    private String phoneNumber;

    @Column(name = "user_email", nullable = false, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus status;

    @Column(name = "user_last_login")
    private LocalDateTime lastLogin;

    @Column(name = "user_signup_date", nullable = false)
    private LocalDate signUpDate;

    @Column(name = "is_oauth", nullable = false)
    private Boolean isOauth;


    // 사용자에서 주소목록을 조회할 경우
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserAddress> userAddresses;

    // 사용자에서 회원등급기록을 조회할 경우
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserRankHistory> userRankHistories;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    public void addOrder(Order order) {
        if (orders == null) {
            orders = new ArrayList<>();
        }
        orders.add(order);
        order.setUser(this); // 양방향 관계 동기화
    }
}

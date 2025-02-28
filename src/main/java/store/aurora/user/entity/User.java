package store.aurora.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @Column(nullable = false, length = 50)
    private String id;

    @Column(name = "user_pwd")
    private String password;

    @NotNull
    @Column(name = "user_name", nullable = false, length = 50)
    private String name;

    @NotNull
    @Column(name = "user_birthday")
    private LocalDate birth;

    @NotNull
    @Column(name = "user_phone_number", nullable = false, length = 13)
    private String phoneNumber;

    @NotNull
    @Column(name = "user_email", nullable = false, length = 100)
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus status;

    @Column(name = "user_last_login")
    private LocalDateTime lastLogin;

    @NotNull
    @Column(name = "user_signup_date", nullable = false)
    private LocalDate signUpDate;

    @NotNull
    @Column(name = "is_oauth", nullable = false)
    private Boolean isOauth;

    // 사용자에서 주소목록을 조회할 경우
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserAddress> userAddresses;

    // 사용자에서 회원등급기록을 조회할 경우
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserRankHistory> userRankHistories;

    @OneToMany(mappedBy = "user")
    private List<UserRole> userRoles;

    public User(String id, String name, LocalDate birth, String phoneNumber, String email, Boolean isOauth) {
        this.id = id;
        this.name = name;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.isOauth = isOauth;
    }

    @PrePersist
    public void setDefaultValues() {
        if (status == null) {
            status = UserStatus.ACTIVE;  // 기본값 'ACTIVE'
        }
        if (signUpDate == null) {
            signUpDate = LocalDate.now();  // 기본값: 현재 날짜
        }
    }
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

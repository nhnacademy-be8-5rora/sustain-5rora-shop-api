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
@Table(name = "user_roles")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    // 사용자 엔터티와 다대일 연결
    @ManyToOne
    private User user;

    // 권한 엔터티와 다대일 연결
    @ManyToOne
    private Role role;
}

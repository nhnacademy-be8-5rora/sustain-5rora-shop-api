package store.aurora.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_rank_histories")
public class UserRankHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    @Column(name = "change_reason", nullable = false)
    private String changeReason;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt = LocalDateTime.now();

    // 유저 엔터티와 다대일 연결
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // 회원등급 엔터티와 다대일 연결
    @ManyToOne(optional = false)
    @JoinColumn(name = "rank_id")
    private UserRank userRank;
}

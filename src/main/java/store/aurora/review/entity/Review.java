package store.aurora.review.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import store.aurora.book.entity.Book;
import store.aurora.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reviews")
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotNull
    @Column(nullable = false)
    private int reviewRating;


    @Setter
    @Column(columnDefinition = "TEXT")
    private String reviewContent;

    @NotNull
    @Setter
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime reviewCreateAt = LocalDateTime.now();

    @NotNull
    @Setter
    @ManyToOne(optional = false)//(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Setter
    @NotNull
    @OneToOne(optional = false)
    private User user;

    @Setter
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> reviewImages;
}

package store.aurora.review.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotNull
    @Column(nullable = false)
    private int reviewRating;

    @Min(1)
    @Max(5)
    @Setter
    @Column(columnDefinition = "TEXT")
    private String reviewContent;

    @NotNull
    @Setter
    @Column(nullable = false)
    private LocalDateTime reviewCreateAt = LocalDateTime.now();

//    @NotNull
//    @Setter
//    @ManyToOne//(fetch = FetchType.LAZY)
//    @JoinColumn(name = "book_id", nullable = false)
//    private Book book;
    private Long BookId;

    @NotNull
    @Setter
    @Column(nullable = false, length = 50)
    private String userId;

//    @Setter
//    @NotNull
//    @OneToOne
//    private User user;

//    @Setter
//    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ReviewImage> reviewImages;
}

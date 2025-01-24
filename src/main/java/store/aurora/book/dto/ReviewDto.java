package store.aurora.book.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long reviewId;
    private String reviewContent;
    private int reviewRating;
    private LocalDateTime reviewCreatedAt;
    private String userName;
    private List<String> reviewImages;

    public ReviewDto(Long reviewId, String userName, String reviewContent, int reviewRating) {
        this.reviewId = reviewId;
        this.userName = userName;
        this.reviewContent = reviewContent;
        this.reviewRating = reviewRating;
    }
}


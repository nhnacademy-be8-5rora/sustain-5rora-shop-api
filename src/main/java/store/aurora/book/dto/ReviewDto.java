package store.aurora.book.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
}


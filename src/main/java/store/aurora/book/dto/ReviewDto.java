package store.aurora.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
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


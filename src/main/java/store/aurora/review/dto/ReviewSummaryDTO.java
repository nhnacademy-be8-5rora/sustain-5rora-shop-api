package store.aurora.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@AllArgsConstructor
@Data
public class ReviewSummaryDTO {
    private int reviewCount;       // 리뷰 개수
    private double averageRating;  // 평균 리뷰 평점
}

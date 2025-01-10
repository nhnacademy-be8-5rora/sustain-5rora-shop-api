package store.aurora.review.dto;

import lombok.*;
import store.aurora.review.entity.ReviewImage;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReviewResponse {
    private Integer rating;
    private String content;
    private List<String> imageFilePath;
    private LocalDateTime reviewCreateAt;
    private Long BookId;
    private String userId;
}

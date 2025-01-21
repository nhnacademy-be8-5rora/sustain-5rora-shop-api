package store.aurora.review.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewResponse {
    private Long id;
    private Integer rating;
    private String content;
    private List<String> imageFilePath;
    private LocalDateTime reviewCreateAt;
    private Long BookId;
    private String title;
    private String author;
    private String cover;
    private String userId;
}

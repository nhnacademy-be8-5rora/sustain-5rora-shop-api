package store.aurora.book.dto.tag;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class TagResponseDto {
    private Long id;   // 태그 ID
    private String name; // 태그 이름
}
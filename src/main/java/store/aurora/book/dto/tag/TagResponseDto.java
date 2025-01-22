package store.aurora.book.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagResponseDto {
    private Long id;   // 태그 ID
    private String name; // 태그 이름
}
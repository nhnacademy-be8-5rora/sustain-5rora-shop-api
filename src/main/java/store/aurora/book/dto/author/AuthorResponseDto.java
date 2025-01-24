package store.aurora.book.dto.author;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "작가 응답 DTO")
public class AuthorResponseDto {
    @Schema(description = "작가 ID", example = "1")
    private Long id;

    @Schema(description = "작가 이름", example = "김영한")
    private String name;
}
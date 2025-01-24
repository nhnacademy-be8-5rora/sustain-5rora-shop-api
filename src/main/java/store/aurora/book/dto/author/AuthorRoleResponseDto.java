package store.aurora.book.dto.author;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "작가 역할 응답 DTO")
public class AuthorRoleResponseDto {
    @Schema(description = "작가 역할 ID", example = "1")
    private Long id;

    @Schema(description = "작가 역할 이름", example = "지은이")
    private String role;
}
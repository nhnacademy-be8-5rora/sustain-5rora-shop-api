package store.aurora.book.dto.author;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "작가 역할 생성 요청 DTO")
public class AuthorRoleRequestDto {
    @Schema(description = "작가 역할", example = "지은이")
    @NotBlank(message = "작가 역할명은 필수 항목입니다.")
    @Size(max = 10, message = "작가 역할명은 최대 10자까지 입력 가능합니다.")
    private String role;
}
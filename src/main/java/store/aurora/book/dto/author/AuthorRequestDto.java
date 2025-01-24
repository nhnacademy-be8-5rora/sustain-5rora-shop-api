package store.aurora.book.dto.author;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "작가 생성 요청 DTO")
public class AuthorRequestDto {
    @Schema(description = "작가 이름", example = "김영한")
    @NotBlank(message = "작가 이름은 필수 항목입니다.")
    @Size(max = 30, message = "작가 이름은 최대 30자까지 입력 가능합니다.")
    private String name;
}
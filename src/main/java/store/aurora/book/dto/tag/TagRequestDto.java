package store.aurora.book.dto.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "태그 생성 요청 DTO")
public class TagRequestDto {

    @Schema(description = "태그 이름", example = "소설")
    @NotBlank(message = "태그 이름은 필수입니다.")
    @Size(max = 15, message = "태그 이름은 최대 15자까지 입력 가능합니다.")
    private String name;

}
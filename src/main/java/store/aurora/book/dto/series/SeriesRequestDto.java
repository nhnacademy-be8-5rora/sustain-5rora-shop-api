package store.aurora.book.dto.series;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "시리즈 생성 요청 DTO")
public class SeriesRequestDto {

    @Schema(description = "시리즈 이름", example = "해리포터")
    @NotBlank(message = "시리즈 이름은 필수입니다.")
    @Size(max = 100, message = "시리즈 이름은 최대 100자까지 입력할 수 있습니다.")
    private String name;

}

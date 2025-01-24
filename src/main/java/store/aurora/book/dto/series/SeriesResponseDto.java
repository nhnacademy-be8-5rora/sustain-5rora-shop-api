package store.aurora.book.dto.series;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "시리즈 응답 DTO")
public class SeriesResponseDto {

    @Schema(description = "시리즈 ID", example = "1")
    private Long id;

    @Schema(description = "시리즈 이름", example = "해리포터")
    private String name;
}

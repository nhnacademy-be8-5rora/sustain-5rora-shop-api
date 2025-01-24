package store.aurora.book.dto.publisher;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "출판사 응답 DTO")
public class PublisherResponseDto {
    @Schema(description = "출판사 ID", example = "1")
    private Long id;

    @Schema(description = "출판사 이름", example = "문학동네")
    private String name;
}
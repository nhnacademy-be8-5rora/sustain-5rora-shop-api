package store.aurora.book.dto.publisher;

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
@Schema(description = "출판사 생성 요청 DTO")  // 전체 클래스 설명 추가
public class PublisherRequestDto {
    @Schema(description = "출판사 이름", example = "문학동네") // 필드에 대한 설명 추가
    @NotBlank(message = "출판사 이름은 필수입니다.")
    @Size(max = 50, message = "출판사 이름은 최대 50자까지 입력 가능합니다.")
    private String name;
}

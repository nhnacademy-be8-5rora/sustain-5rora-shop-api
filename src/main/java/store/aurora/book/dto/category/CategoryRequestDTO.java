package store.aurora.book.dto.category;

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
@Schema(description = "카테고리 생성 요청 DTO")
public class CategoryRequestDTO {

    @Schema(description = "부모 카테고리 ID", example = "1")
    private Long parentId;

    @Schema(description = "카테고리 이름", example = "문학")
    @NotBlank(message = "카테고리 이름은 필수입니다.")
    @Size(max = 20, message = "카테고리 이름은 최대 20자까지 입력 가능합니다.")
    private String name;
}

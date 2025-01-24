package store.aurora.book.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "카테고리 응답 DTO")
public class CategoryResponseDTO {

    @Schema(description = "카테고리 ID", example = "1")
    private Long id;

    @Schema(description = "카테고리 이름", example = "문학")
    private String name;

    @Schema(description = "부모 카테고리 ID", example = "null")
    private Long parentId;

    @Schema(description = "부모 카테고리 이름", example = "null")
    private String parentName;

    @Schema(description = "카테고리 깊이", example = "0")
    private int depth;

    @Schema(description = "하위 카테고리 목록")
    private List<CategoryResponseDTO> children = new ArrayList<>();

    public CategoryResponseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}

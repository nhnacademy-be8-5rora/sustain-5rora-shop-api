package store.aurora.book.dto.category;

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
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private Long parentId;
    private String parentName;
    private int depth;
    private List<CategoryResponseDTO> children = new ArrayList<>();

    public CategoryResponseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}

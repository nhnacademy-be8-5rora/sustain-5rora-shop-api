package store.aurora.book.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class CategoryRequestDTO {
    private Long parentId;
    @NotBlank
    @Size(max = 20)
    private String name;
}

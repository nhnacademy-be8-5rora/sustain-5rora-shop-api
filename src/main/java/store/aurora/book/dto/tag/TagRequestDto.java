package store.aurora.book.dto.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagRequestDto {
    @NotBlank
    @Size(max = 15)
    private String name;
}
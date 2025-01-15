package store.aurora.book.dto.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagRequestDto {
    @NotBlank(message = "태그 이름은 필수입니다.")
    @Size(max = 15, message = "태그 이름은 최대 15자까지 입력 가능합니다.")
    private String name;
}
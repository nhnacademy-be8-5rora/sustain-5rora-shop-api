package store.aurora.book.dto.author;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorRoleRequestDto {

    @NotBlank(message = "작가 역할명은 필수 항목입니다.")
    @Size(max = 10, message = "작가 역할명은 최대 10자까지 입력 가능합니다.")
    private String role;
}
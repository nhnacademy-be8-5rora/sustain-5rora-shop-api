package store.aurora.book.dto.author;

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
public class AuthorRequestDto {

    @NotBlank(message = "작가 이름은 필수 항목입니다.")
    @Size(max = 30, message = "작가 이름은 최대 30자까지 입력 가능합니다.")
    private String name;
}
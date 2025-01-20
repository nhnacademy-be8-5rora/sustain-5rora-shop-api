package store.aurora.book.dto.author;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorResponseDto {
    private Long id;
    private String name;
}

package store.aurora.book.dto.author;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorRoleResponseDto {
    private Long id;
    private String role;
}
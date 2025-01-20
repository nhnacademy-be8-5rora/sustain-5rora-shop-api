package store.aurora.book.dto.author;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParsedAuthorDto {
    private String name;
    private String role;
}
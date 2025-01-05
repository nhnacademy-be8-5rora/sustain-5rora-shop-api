package store.aurora.book.dto.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookCategoryDto {
    private Long id;
    private String name;
    private Integer depth;
    private List<BookCategoryDto> children = new ArrayList<>();
}


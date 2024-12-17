package store.aurora.book.dto.category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BookCategoryRequestDTO {
    private List<Long> categoryIds;
}

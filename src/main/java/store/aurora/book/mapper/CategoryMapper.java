package store.aurora.book.mapper;

import store.aurora.book.dto.CategoryResponseDTO;
import store.aurora.book.entity.Category;

public class CategoryMapper {

    public static CategoryResponseDTO toResponseDTO(Category category) {
        Long parentId = category.getParent() != null ? category.getParent().getId() : null;
        String parentName = category.getParent() != null ? category.getParent().getName() : null;

        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                parentId,
                parentName,
                category.getDepth()
        );
    }
}

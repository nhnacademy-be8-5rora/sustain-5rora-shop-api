package store.aurora.book.service.category;

import store.aurora.book.dto.category.CategoryRequestDTO;
import store.aurora.book.dto.category.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {
    void createCategory(CategoryRequestDTO requestDTO);
    void updateCategoryName(Long categoryId, String newName);
    void deleteCategory(Long categoryId);
    List<CategoryResponseDTO> getAllCategories(); // 새로 추가

}

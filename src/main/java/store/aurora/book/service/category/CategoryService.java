package store.aurora.book.service.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.aurora.book.dto.category.CategoryDTO;
import store.aurora.book.dto.category.CategoryRequestDTO;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.category.BookCategory;

import java.util.List;

public interface CategoryService {
    Page<CategoryResponseDTO> getPagedCategories(Pageable pageable);

    List<CategoryResponseDTO> getChildrenCategories(Long parentId);

    Page<CategoryResponseDTO> getPagedChildrenCategories(Long parentId, Pageable pageable);

    List<CategoryResponseDTO> getRootCategories();

    Page<CategoryResponseDTO> getPagedRootCategories(Pageable pageable);

    List<CategoryResponseDTO> getCategoryHierarchy();

    void createCategory(CategoryRequestDTO requestDTO);
    void updateCategoryName(Long categoryId, String newName);
    void deleteCategory(Long categoryId);
    List<CategoryResponseDTO> getAllCategories(); // 새로 추가
    List<Book> getBooksByCategoryId(Long categoryId);
    CategoryDTO findById(Long categoryId);
    List<BookCategory> createBookCategories(List<Long> categoryIds);
    List<CategoryResponseDTO> getRootCategories();
}

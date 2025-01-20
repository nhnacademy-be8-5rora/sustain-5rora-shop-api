package store.aurora.book.service.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.category.CategoryRequestDTO;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.category.BookCategory;

import java.util.List;

public interface CategoryService {
    void createCategory(CategoryRequestDTO requestDTO);
    void updateCategoryName(Long categoryId, String newName);
    void deleteCategory(Long categoryId);

    Page<CategoryResponseDTO> getRootCategories(Pageable pageable);

    List<CategoryResponseDTO> getAllRootCategories();

    Page<CategoryResponseDTO> getChildrenCategories(Long parentId, Pageable pageable);

    List<CategoryResponseDTO> getAllChildrenCategories(Long parentId);

    List<Book> getBooksByCategoryId(Long categoryId);
    CategoryResponseDTO findById(Long categoryId);
    List<BookCategory> createBookCategories(List<Long> categoryIds);

    void updateBookCategories(Book book, List<Long> categoryIds);

    List<CategoryResponseDTO> getCategories();
}

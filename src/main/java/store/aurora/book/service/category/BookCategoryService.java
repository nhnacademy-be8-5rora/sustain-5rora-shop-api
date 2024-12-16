package store.aurora.book.service.category;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.category.BookCategory;
import store.aurora.book.entity.category.Category;
import store.aurora.book.exception.book.NotFoundBookException;
import store.aurora.book.exception.category.CategoryLimitException;
import store.aurora.book.exception.category.InvalidCategoryException;
import store.aurora.book.repository.category.BookCategoryRepository;
import store.aurora.book.repository.BookRepository;
import store.aurora.book.repository.category.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookCategoryService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;

    @Transactional
    public void addCategoriesToBook(Long bookId, List<Long> categoryIds) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundBookException(bookId));

        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new InvalidCategoryException("유효하지 않은 카테고리 ID가 포함되어 있습니다.");
        }

        for (Category category : categories) {
            if (book.getBookCategories().stream().noneMatch(bookCategory -> bookCategory.getCategory().equals(category))) {
                BookCategory bookCategory = new BookCategory();
                bookCategory.setBook(book);
                bookCategory.setCategory(category);
                book.getBookCategories().add(bookCategory);
            }
        }

        bookCategoryRepository.saveAll(book.getBookCategories());
    }

    @Transactional
    public void removeCategoriesFromBook(Long bookId, List<Long> categoryIds) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundBookException(bookId));

        int currentCategoryCount = book.getBookCategories().size();
        if (currentCategoryCount - categoryIds.size() <= 0) {
            throw new CategoryLimitException();
        }
        book.getBookCategories().removeIf(bookCategory ->
                categoryIds.contains(bookCategory.getCategory().getId()));
    }

    public List<Category> getCategoriesByBookId(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundBookException(bookId));

        return book.getBookCategories()
                .stream()
                .map(BookCategory::getCategory)
                .collect(Collectors.toList());
    }
}

package store.aurora.book.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        if (!bookRepository.existsById(bookId)) {
            throw new NotFoundBookException(bookId);
        }

        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new InvalidCategoryException("유효하지 않은 카테고리 ID가 포함되어 있습니다.");
        }

        for (Category category : categories) {
            boolean alreadyExists = bookCategoryRepository.existsByBookIdAndCategoryId(bookId, category.getId());
            if (!alreadyExists) {
                BookCategory bookCategory = new BookCategory();
                bookCategory.setBookId(bookId);
                bookCategory.setCategory(category);
                bookCategoryRepository.save(bookCategory);
            }
        }
    }

    @Transactional
    public void removeCategoriesFromBook(Long bookId, List<Long> categoryIds) {
        // 책 검증
        if (!bookRepository.existsById(bookId)) {
            throw new NotFoundBookException(bookId);
        }

        // 현재 연결된 카테고리 수 확인
        long currentCategoryCount = bookCategoryRepository.countByBookId(bookId);
        if (currentCategoryCount - categoryIds.size() <= 0) {
            throw new CategoryLimitException();
        }

        // BookCategory 삭제
        bookCategoryRepository.deleteByBookIdAndCategoryIdIn(bookId, categoryIds);
    }

    @Transactional(readOnly = true)
    public List<Category> getCategoriesByBookId(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new NotFoundBookException(bookId);
        }

        return bookCategoryRepository.findByBookId(bookId).stream()
                .map(BookCategory::getCategory)
                .collect(Collectors.toList());
    }
}
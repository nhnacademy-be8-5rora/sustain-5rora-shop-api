package store.aurora.book.service.category.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.category.BookCategory;
import store.aurora.book.entity.category.Category;
import store.aurora.book.exception.book.NotFoundBookException;
import store.aurora.book.exception.category.CategoryLimitException;
import store.aurora.book.exception.category.CategoryNotFoundException;
import store.aurora.book.exception.category.InvalidCategoryException;
import store.aurora.book.repository.category.BookCategoryRepository;
import store.aurora.book.repository.BookRepository;
import store.aurora.book.repository.category.CategoryRepository;
import store.aurora.book.service.category.BookCategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookCategoryServiceImpl implements BookCategoryService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;

    @Transactional
    public void addCategoriesToBook(Long bookId, List<Long> categoryIds) {
        Book book = findBookById(bookId);

        // 유효한 카테고리 검증 및 조회
        List<Category> categories = validateAndGetCategories(categoryIds);

        // 이미 추가된 카테고리를 필터링
        List<Category> existingCategories = book.getBookCategories().stream()
                .map(BookCategory::getCategory)
                .toList();

        List<BookCategory> newBookCategories = categories.stream()
                .filter(category -> !existingCategories.contains(category))
                .map(category -> createBookCategory(book, category))
                .toList();

        // 새로운 카테고리가 있을 경우 저장
        if (!newBookCategories.isEmpty()) {
            bookCategoryRepository.saveAll(newBookCategories);
        }
    }

    @Transactional
    public void removeCategoriesFromBook(Long bookId, List<Long> categoryIds) {
        Book book = findBookById(bookId);

        // 삭제할 `BookCategory` 찾기
        List<BookCategory> bookCategoriesToDelete = book.getBookCategories().stream()
                .filter(bookCategory -> categoryIds.contains(bookCategory.getCategory().getId()))
                .toList();

        if (bookCategoriesToDelete.isEmpty()) {
            throw new CategoryNotFoundException("삭제할 카테고리가 존재하지 않습니다.");
        }

        // 최소 하나의 카테고리는 유지해야 함
        long remainingCategories = book.getBookCategories().size() - bookCategoriesToDelete.size();
        if (remainingCategories <= 0) {
            throw new CategoryLimitException();
        }

        // 양방향 동기화를 통해 제거
        for (BookCategory bookCategory : bookCategoriesToDelete) {
            book.removeBookCategory(bookCategory);
            bookCategory.getCategory().removeBookCategory(bookCategory);
        }

        bookCategoryRepository.deleteAll(bookCategoriesToDelete);
    }

    @Transactional(readOnly = true)
    public List<Category> getCategoriesByBookId(Long bookId) {
        Book book = findBookById(bookId);

        // 책에 연결된 카테고리 리스트 반환
        return book.getBookCategories().stream()
                .map(BookCategory::getCategory)
                .toList();
    }

    private Book findBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundBookException(bookId));
    }

    private List<Category> validateAndGetCategories(List<Long> categoryIds) {
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new InvalidCategoryException("유효하지 않은 카테고리 ID가 포함되어 있습니다.");
        }
        return categories;
    }

    private BookCategory createBookCategory(Book book, Category category) {
        BookCategory bookCategory = new BookCategory();
        bookCategory.setBook(book);
        bookCategory.setCategory(category);

        // 양방향 동기화
        book.addBookCategory(bookCategory);
        category.addBookCategory(bookCategory);

        return bookCategory;
    }
}

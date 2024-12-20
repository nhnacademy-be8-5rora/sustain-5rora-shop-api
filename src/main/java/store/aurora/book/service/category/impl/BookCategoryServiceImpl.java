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

import java.util.ArrayList;
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

        validateCategoryIds(categoryIds); // ID 검증만 수행

        List<Long> existingCategoryIds = book.getBookCategories()
                .stream()
                .map(bookCategory -> bookCategory.getCategory().getId())
                .toList();

        List<BookCategory> newBookCategories = categoryIds.stream()
                .filter(categoryId -> !existingCategoryIds.contains(categoryId))
                .map(categoryId -> createBookCategory(book, categoryId))
                .toList();

        if (!newBookCategories.isEmpty()) {
            bookCategoryRepository.saveAll(newBookCategories);
        }
    }

    @Transactional
    public void removeCategoriesFromBook(Long bookId, List<Long> categoryIds) {
        Book book = findBookById(bookId);

        List<BookCategory> bookCategoriesToDelete = bookCategoryRepository.findByBookIdAndCategoryIdIn(bookId, categoryIds);
        if (bookCategoriesToDelete.isEmpty()) {
            throw new CategoryNotFoundException("삭제할 카테고리가 존재하지 않습니다.");
        }

        long currentCategoryCount = book.getBookCategories().size();
        if (currentCategoryCount - bookCategoriesToDelete.size() <= 0) {
            throw new CategoryLimitException();
        }

        bookCategoriesToDelete.forEach(book::removeBookCategory); // 양방향 동기화

        bookCategoryRepository.deleteAll(bookCategoriesToDelete);
    }



    @Transactional(readOnly = true)
    public List<Category> getCategoriesByBookId(Long bookId) {
        Book book = findBookById(bookId);

        return book.getBookCategories().stream()
                .map(BookCategory::getCategory)
                .toList();
    }

    private Book findBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundBookException(bookId));
    }

    private void validateCategoryIds(List<Long> categoryIds) {
        long validCount = categoryRepository.countByIdIn(categoryIds);
        if (validCount != categoryIds.size()) {
            throw new InvalidCategoryException("유효하지 않은 카테고리 ID가 포함되어 있습니다.");
        }
    }

    private BookCategory createBookCategory(Book book, Category category) {
        BookCategory bookCategory = new BookCategory();
        bookCategory.setBook(book);
        bookCategory.setCategory(category);

        book.addBookCategory(bookCategory); // 양방향 동기화
        category.addBookCategory(bookCategory);

        return bookCategory;
    }
}
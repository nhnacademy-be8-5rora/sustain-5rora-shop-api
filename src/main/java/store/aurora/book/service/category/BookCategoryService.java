package store.aurora.book.service.category;

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

import java.util.ArrayList;
import java.util.List;

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

        List<BookCategory> existingBookCategories = bookCategoryRepository.findByBookIdAndCategoryIdIn(bookId, categoryIds);
        List<Long> existingCategoryIds = new ArrayList<>();
        for (BookCategory bookCategory : existingBookCategories) {
            existingCategoryIds.add(bookCategory.getCategory().getId());
        }

        List<BookCategory> newBookCategories = new ArrayList<>();
        for (Category category : categories) {
            if (!existingCategoryIds.contains(category.getId())) {
                newBookCategories.add(new BookCategory(null, book, category));
            }
        }
        if (!newBookCategories.isEmpty()) {
            bookCategoryRepository.saveAll(newBookCategories);
        }
    }

    @Transactional
    public void removeCategoriesFromBook(Long bookId, List<Long> categoryIds) {
        if (!bookRepository.existsById(bookId)) {
            throw new NotFoundBookException(bookId);
        }

        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new InvalidCategoryException("유효하지 않은 카테고리 ID가 포함되어 있습니다.");
        }

        List<BookCategory> bookCategoriesToDelete = bookCategoryRepository.findByBookIdAndCategoryIdIn(bookId, categoryIds);
        if (bookCategoriesToDelete.isEmpty()) {
            throw new CategoryNotFoundException("삭제할 카테고리가 존재하지 않습니다.");
        }

        long currentCategoryCount = bookCategoryRepository.countByBookId(bookId);
        if (currentCategoryCount - bookCategoriesToDelete.size() <= 0) {
            throw new CategoryLimitException();
        }

        bookCategoryRepository.deleteAll(bookCategoriesToDelete);
    }



    @Transactional(readOnly = true)
    public List<Category> getCategoriesByBookId(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new NotFoundBookException(bookId);
        }

        List<BookCategory> bookCategories = bookCategoryRepository.findByBookId(bookId);
        List<Category> categories = new ArrayList<>();
        for (BookCategory bookCategory : bookCategories) {
            categories.add(bookCategory.getCategory());
        }
        return categories;
    }
}
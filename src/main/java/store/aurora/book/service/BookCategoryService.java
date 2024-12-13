package store.aurora.book.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookCategory;
import store.aurora.book.entity.Category;
import store.aurora.book.repository.BookCategoryRepository;
import store.aurora.book.repository.BookRepository;
import store.aurora.book.repository.CategoryRepository;

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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책 ID입니다: " + bookId));

        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID입니다: " + categoryId));

            boolean alreadyExists = book.getBookCategories()
                    .stream()
                    .anyMatch(bookCategory -> bookCategory.getCategory().getId().equals(categoryId));
            if (!alreadyExists) {
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책 ID입니다: " + bookId));

        book.getBookCategories().removeIf(bookCategory ->
                categoryIds.contains(bookCategory.getCategory().getId()));
    }

    public List<Category> getCategoriesByBookId(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책 ID입니다: " + bookId));

        return book.getBookCategories()
                .stream()
                .map(BookCategory::getCategory)
                .collect(Collectors.toList());
    }
}

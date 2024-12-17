package store.aurora.book.service.category;

import store.aurora.book.entity.category.Category;

import java.util.List;

public interface BookCategoryService {
    void addCategoriesToBook(Long bookId, List<Long> categoryIds);
    void removeCategoriesFromBook(Long bookId, List<Long> categoryIds);
    List<Category> getCategoriesByBookId(Long bookId);
}
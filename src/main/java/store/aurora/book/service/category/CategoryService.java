package store.aurora.book.service.category;

public interface CategoryService {
    void createCategory(String name, Long parentId);
    void updateCategoryName(Long categoryId, String newName);
    void deleteCategory(Long categoryId);
}

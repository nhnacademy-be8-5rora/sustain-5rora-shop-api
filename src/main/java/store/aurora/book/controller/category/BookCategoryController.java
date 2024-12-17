package store.aurora.book.controller.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.category.BookCategoryRequestDTO;
import store.aurora.book.entity.category.Category;
import store.aurora.book.service.category.BookCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/book-category")
@RequiredArgsConstructor
public class BookCategoryController {
    private final BookCategoryService bookCategoryService;

    @PostMapping("{bookId}")
    public ResponseEntity<Void> addCategoryToBook(@PathVariable Long bookId, @RequestBody BookCategoryRequestDTO request) {
        bookCategoryService.addCategoriesToBook(bookId, request.getCategoryIds());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{bookId}")
    public ResponseEntity<Void> removeCategoryFromBook(@PathVariable Long bookId,@RequestBody BookCategoryRequestDTO request) {
        bookCategoryService.removeCategoriesFromBook(bookId, request.getCategoryIds());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<List<Category>> getCategoriesByBook(@PathVariable Long bookId) {
        List<Category> categories = bookCategoryService.getCategoriesByBookId(bookId);
        return ResponseEntity.ok(categories);
    }
}
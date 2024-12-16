package store.aurora.book.controller.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.category.BookCategoryRequestDTO;
import store.aurora.book.entity.Category;
import store.aurora.book.service.BookCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/book-category")
@RequiredArgsConstructor
public class BookCategoryController {
    private final BookCategoryService bookCategoryService;

    @PostMapping
    public ResponseEntity<Void> addCategoryToBook(@RequestBody BookCategoryRequestDTO request) {
        bookCategoryService.addCategoriesToBook(request.getBookId(), request.getCategoryIds());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeCategoryFromBook(@RequestBody BookCategoryRequestDTO request) {
        bookCategoryService.removeCategoriesFromBook(request.getBookId(), request.getCategoryIds());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<List<Category>> getCategoriesByBook(@PathVariable Long bookId) {
        List<Category> categories = bookCategoryService.getCategoriesByBookId(bookId);
        return ResponseEntity.ok(categories);
    }
}
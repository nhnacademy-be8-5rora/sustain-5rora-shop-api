package store.aurora.book.controller.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.category.CategoryRequestDTO;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.book.dto.response.BookResponseDTO;
import store.aurora.book.entity.Book;

import store.aurora.book.service.category.CategoryService;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;


    @GetMapping("/root")
    public ResponseEntity<Page<CategoryResponseDTO>> getRootCategories(Pageable pageable) {
        return ResponseEntity.ok(categoryService.getRootCategories(pageable));
    }
    @GetMapping("/root/all")
    public ResponseEntity<List<CategoryResponseDTO>> getAllRootCategories() {
        return ResponseEntity.ok(categoryService.getAllRootCategories());
    }

    @GetMapping("/{parentId}/children")
    public ResponseEntity<Page<CategoryResponseDTO>> getChildrenCategories(@PathVariable Long parentId,
                                                                                Pageable pageable) {
        return ResponseEntity.ok(categoryService.getChildrenCategories(parentId, pageable));
    }
    @GetMapping("/{parentId}/children/all")
    public ResponseEntity<List<CategoryResponseDTO>> getAllChildrenCategories(@PathVariable Long parentId) {
        return ResponseEntity.ok(categoryService.getAllChildrenCategories(parentId));
    }


    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }

    @PostMapping
    public ResponseEntity<Void> createCategory(@RequestBody @Valid CategoryRequestDTO requestDTO) {
        categoryService.createCategory(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<Void> updateCategoryName(@PathVariable Long categoryId,
                                                                  @RequestBody @Valid CategoryRequestDTO requestDTO) {
        categoryService.updateCategoryName(categoryId, requestDTO.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{categoryId}/books")
    public ResponseEntity<List<BookResponseDTO>> getBooksByCategory(@PathVariable Long categoryId) {
        List<Book> books = categoryService.getBooksByCategoryId(categoryId);
        List<BookResponseDTO> response = books.stream()
                .map(book -> new BookResponseDTO(book.getId(), book.getTitle()))
                .toList();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> getCategoriesByParentId(@PathVariable Long categoryId) {
        CategoryResponseDTO categoryList = categoryService.findById(Objects.requireNonNullElse(categoryId, 0L));
        return ResponseEntity.ok(categoryList);
    }
}
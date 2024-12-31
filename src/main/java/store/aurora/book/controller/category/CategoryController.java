package store.aurora.book.controller.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.category.CategoryDTO;
import store.aurora.book.dto.category.CategoryRequestDTO;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.book.dto.response.BookResponseDTO;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.category.Category;
//import store.aurora.book.mapper.CategoryMapper;
import store.aurora.book.service.category.CategoryService;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // 계층형 카테고리 데이터 반환
    @GetMapping("/hierarchy")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoryHierarchy() {
        List<CategoryResponseDTO> hierarchy = categoryService.getCategoryHierarchy();
        return ResponseEntity.ok(hierarchy);
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
    public ResponseEntity<CategoryDTO> getCategoriesByParentId(@PathVariable Long categoryId) {
        CategoryDTO categoryList = categoryService.findById(Objects.requireNonNullElse(categoryId, 0L));
        return ResponseEntity.ok(categoryList);
    }
}
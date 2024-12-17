package store.aurora.book.controller.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.category.CategoryRequestDTO;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.book.entity.category.Category;
import store.aurora.book.mapper.CategoryMapper;
import store.aurora.book.service.category.CategoryService;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Void> createCategory(@RequestBody @Valid CategoryRequestDTO requestDTO) {
        categoryService.createCategory(requestDTO.getName(), requestDTO.getParentId());
        return ResponseEntity.ok().build();
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
}
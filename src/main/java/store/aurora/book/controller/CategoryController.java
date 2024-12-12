package store.aurora.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.CategoryRequestDTO;
import store.aurora.book.entity.Category;
import store.aurora.book.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody @Valid CategoryRequestDTO requestDTO) {
        Category category = categoryService.createCategory(requestDTO.getName(), requestDTO.getParentId());
        return ResponseEntity.ok(category);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<Category> updateCategoryName(@PathVariable Long categoryId,
                                                       @RequestParam String newName) {
        Category updatedCategory = categoryService.updateCategoryName(categoryId, newName);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
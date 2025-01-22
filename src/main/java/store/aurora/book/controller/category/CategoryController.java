package store.aurora.book.controller.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.category.CategoryRequestDTO;
import store.aurora.book.dto.category.CategoryResponseDTO;

import store.aurora.book.service.category.CategoryService;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    //모든 카테고리 재귀적으로 가져오기
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }
    //최상위 카테고리(page)
    @GetMapping("/root")
    public ResponseEntity<Page<CategoryResponseDTO>> getRootCategories(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(categoryService.getRootCategories(pageable));
    }
    //최상위 카테고리
    @GetMapping("/root/all")
    public ResponseEntity<List<CategoryResponseDTO>> getAllRootCategories() {
        return ResponseEntity.ok(categoryService.getAllRootCategories());
    }
    //하위 카테고리(page)
    @GetMapping("/{parent-id}/children")
    public ResponseEntity<Page<CategoryResponseDTO>> getChildrenCategories(@PathVariable("parent-id") Long parentId,
                                                                           @RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(categoryService.getChildrenCategories(parentId, pageable));
    }
    //하위 카테고리
    @GetMapping("/{parent-id}/children/all")
    public ResponseEntity<List<CategoryResponseDTO>> getAllChildrenCategories(@PathVariable("parent-id") Long parentId) {
        return ResponseEntity.ok(categoryService.getAllChildrenCategories(parentId));
    }

    @PostMapping
    public ResponseEntity<Void> createCategory(@RequestBody @Valid CategoryRequestDTO requestDTO) {
        categoryService.createCategory(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{category-id}")
    public ResponseEntity<Void> updateCategory(@PathVariable("category-id") Long categoryId,
                                                                  @RequestBody @Valid CategoryRequestDTO requestDTO) {
        categoryService.updateCategory(categoryId, requestDTO.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{category-id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("category-id") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{category-id}")
    public ResponseEntity<CategoryResponseDTO> getCategoriesByParentId(@PathVariable("category-id") Long categoryId) {
        CategoryResponseDTO categoryList = categoryService.findById(Objects.requireNonNullElse(categoryId, 0L));
        return ResponseEntity.ok(categoryList);
    }
}
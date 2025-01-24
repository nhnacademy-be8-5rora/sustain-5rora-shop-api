package store.aurora.book.controller.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Category API", description = "도서 카테고리 관리 API")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "모든 카테고리 조회", description = "모든 카테고리를 재귀적으로 가져옵니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 카테고리 목록을 반환함",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDTO.class)))
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }

    @Operation(summary = "최상위 카테고리 조회 (페이징)", description = "최상위 카테고리를 페이지네이션하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 최상위 카테고리 목록을 반환함",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDTO.class)))
    @GetMapping("/root")
    public ResponseEntity<Page<CategoryResponseDTO>> getRootCategories(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(categoryService.getRootCategories(pageable));
    }

    @Operation(summary = "최상위 카테고리 전체 조회", description = "최상위 카테고리 전체를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 최상위 카테고리 목록을 반환함")
    @GetMapping("/root/all")
    public ResponseEntity<List<CategoryResponseDTO>> getAllRootCategories() {
        return ResponseEntity.ok(categoryService.getAllRootCategories());
    }

    @Operation(summary = "하위 카테고리 조회 (페이징)", description = "부모 카테고리 ID를 기준으로 하위 카테고리를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 하위 카테고리 목록을 반환함")
    @GetMapping("/{parent-id}/children")
    public ResponseEntity<Page<CategoryResponseDTO>> getChildrenCategories(@PathVariable("parent-id") Long parentId,
                                                                           @RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(categoryService.getChildrenCategories(parentId, pageable));
    }

    @Operation(summary = "하위 카테고리 전체 조회", description = "부모 카테고리 ID를 기준으로 하위 카테고리 전체를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 하위 카테고리 목록을 반환함")
    @GetMapping("/{parent-id}/children/all")
    public ResponseEntity<List<CategoryResponseDTO>> getAllChildrenCategories(@PathVariable("parent-id") Long parentId) {
        return ResponseEntity.ok(categoryService.getAllChildrenCategories(parentId));
    }

    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 추가합니다.")
    @ApiResponse(responseCode = "201", description = "카테고리 생성 성공")
    @ApiResponse(responseCode = "409", description = "이미 존재하는 카테고리 (CategoryAlreadyExistException)")
    @PostMapping
    public ResponseEntity<Void> createCategory(@RequestBody @Valid CategoryRequestDTO requestDTO) {
        categoryService.createCategory(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "카테고리 수정", description = "ID를 기반으로 카테고리를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "카테고리 수정 성공")
    @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음 (CategoryNotFoundException)")
    @PatchMapping("/{category-id}")
    public ResponseEntity<Void> updateCategory(@PathVariable("category-id") Long categoryId,
                                               @RequestBody @Valid CategoryRequestDTO requestDTO) {
        categoryService.updateCategory(categoryId, requestDTO.getName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "카테고리 삭제", description = "ID를 기반으로 카테고리를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "카테고리 삭제 성공")
    @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음 (CategoryNotFoundException)")
    @ApiResponse(responseCode = "409", description = "하위 카테고리가 존재하여 삭제 불가 (SubCategoryExistsException)")
    @DeleteMapping("/{category-id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("category-id") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "카테고리 상세 조회", description = "ID를 기반으로 특정 카테고리 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "카테고리 정보 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음 (CategoryNotFoundException)")
    @GetMapping("/{category-id}")
    public ResponseEntity<CategoryResponseDTO> getCategoriesByParentId(@PathVariable("category-id") Long categoryId) {
        CategoryResponseDTO categoryList = categoryService.findById(Objects.requireNonNullElse(categoryId, 0L));
        return ResponseEntity.ok(categoryList);
    }
}
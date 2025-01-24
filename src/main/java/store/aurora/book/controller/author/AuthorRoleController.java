package store.aurora.book.controller.author;

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
import store.aurora.book.dto.author.AuthorRoleRequestDto;
import store.aurora.book.dto.author.AuthorRoleResponseDto;
import store.aurora.book.service.author.AuthorRoleService;

@RestController
@RequestMapping("/api/author-roles")
@RequiredArgsConstructor
@Tag(name = "Author Role API", description = "작가 역할 관리 API")
public class AuthorRoleController {

    private final AuthorRoleService authorRoleService;

    @Operation(summary = "모든 작가 역할 조회", description = "페이지네이션을 적용하여 모든 작가 역할을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 작가 역할 목록을 반환함", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorRoleResponseDto.class)))
    @GetMapping
    public ResponseEntity<Page<AuthorRoleResponseDto>> getAllRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(authorRoleService.getAllRoles(pageable));
    }

    @Operation(summary = "작가 역할 상세 조회", description = "ID를 기반으로 작가 역할의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "작가 역할 정보 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorRoleResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "작가 역할을 찾을 수 없음 (AuthorRoleNotFoundException)")
    @GetMapping("/{author-role-id}")
    public ResponseEntity<AuthorRoleResponseDto> getRoleById(@PathVariable("author-role-id") Long id) {
        return ResponseEntity.ok(authorRoleService.getRoleById(id));
    }

    @Operation(summary = "작가 역할 생성", description = "새로운 작가 역할을 추가합니다.")
    @ApiResponse(responseCode = "201", description = "작가 역할 생성 성공")
    @ApiResponse(responseCode = "409", description = "이미 존재하는 작가 역할 (AuthorRoleAlreadyExistsException)")
    @PostMapping
    public ResponseEntity<Void> createRole(@Valid @RequestBody AuthorRoleRequestDto requestDto) {
        authorRoleService.createRole(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "작가 역할 정보 수정", description = "ID를 기반으로 작가 역할 정보를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "작가 역할 정보 수정 성공")
    @ApiResponse(responseCode = "404", description = "작가 역할을 찾을 수 없음 (AuthorRoleNotFoundException)")
    @ApiResponse(responseCode = "409", description = "이미 존재하는 작가 역할 (AuthorRoleAlreadyExistsException)")
    @PutMapping("/{author-role-id}")
    public ResponseEntity<Void> updateRole(
            @PathVariable("author-role-id") Long id,
            @Valid @RequestBody AuthorRoleRequestDto requestDto) {
        authorRoleService.updateRole(id, requestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "작가 역할 삭제", description = "ID를 기반으로 작가 역할 정보를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "작가 역할 삭제 성공")
    @ApiResponse(responseCode = "404", description = "작가 역할을 찾을 수 없음 (AuthorRoleNotFoundException)")
    @ApiResponse(responseCode = "409", description = "작가 역할과 연결된 책이 있어 삭제 불가 (AuthorRoleLinkedToBooksException)")
    @DeleteMapping("/{author-role-id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("author-role-id") Long id) {
        authorRoleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
package store.aurora.book.controller.author;

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
public class AuthorRoleController {

    private final AuthorRoleService authorRoleService;

    @GetMapping
    public ResponseEntity<Page<AuthorRoleResponseDto>> getAllRoles(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(authorRoleService.getAllRoles(pageable));
    }

    @GetMapping("/{author-role-id}")
    public ResponseEntity<AuthorRoleResponseDto> getRoleById(@PathVariable("author-role-id") Long id) {
        return ResponseEntity.ok(authorRoleService.getRoleById(id));
    }

    @PostMapping
    public ResponseEntity<Void> createRole(@Valid @RequestBody AuthorRoleRequestDto requestDto) {
        authorRoleService.createRole(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{author-role-id}")
    public ResponseEntity<Void> updateRole(@PathVariable("author-role-id") Long id, @Valid @RequestBody AuthorRoleRequestDto requestDto) {
        authorRoleService.updateRole(id, requestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{author-role-id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("author-role-id") Long id) {
        authorRoleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
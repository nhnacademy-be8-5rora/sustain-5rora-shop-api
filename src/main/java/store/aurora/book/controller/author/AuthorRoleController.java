package store.aurora.book.controller.author;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.entity.AuthorRole;
import store.aurora.book.service.AuthorRoleService;

import java.util.List;

@RestController
@RequestMapping("/api/author-roles")
@RequiredArgsConstructor
public class AuthorRoleController {

    private final AuthorRoleService authorRoleService;

    @GetMapping
    public ResponseEntity<List<AuthorRole>> getAllRoles() {
        return ResponseEntity.ok(authorRoleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorRole> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(authorRoleService.getRoleById(id));
    }

    @PostMapping
    public ResponseEntity<AuthorRole> createRole(@RequestBody AuthorRole role) {
        return ResponseEntity.ok(authorRoleService.createRole(role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        authorRoleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}

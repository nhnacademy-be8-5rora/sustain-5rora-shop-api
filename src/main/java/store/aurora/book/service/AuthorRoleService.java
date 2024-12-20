package store.aurora.book.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.book.entity.AuthorRole;
import store.aurora.book.repository.AuthorRoleRepository;

import java.util.List;


public interface AuthorRoleService {
    List<AuthorRole> getAllRoles();
    AuthorRole getRoleById(Long id);
    AuthorRole createRole(AuthorRole role);
    void deleteRole(Long id);
}

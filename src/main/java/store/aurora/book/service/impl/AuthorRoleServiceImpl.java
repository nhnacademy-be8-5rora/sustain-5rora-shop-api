package store.aurora.book.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.entity.AuthorRole;
import store.aurora.book.repository.AuthorRoleRepository;
import store.aurora.book.service.AuthorRoleService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthorRoleServiceImpl implements AuthorRoleService {
    private final AuthorRoleRepository authorRoleRepository;

    @Override
    public List<AuthorRole> getAllRoles() {
        return authorRoleRepository.findAll();
    }

    @Override
    public AuthorRole getRoleById(Long id) {
        return authorRoleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
    }

    @Override
    public AuthorRole createRole(AuthorRole role) {
        return authorRoleRepository.save(role);
    }

    @Override
    public void deleteRole(Long id) {
        authorRoleRepository.deleteById(id);
    }
}

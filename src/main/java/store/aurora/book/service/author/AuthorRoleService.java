package store.aurora.book.service.author;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.author.AuthorRoleRequestDto;
import store.aurora.book.dto.author.AuthorRoleResponseDto;
import store.aurora.book.entity.AuthorRole;

public interface AuthorRoleService {

    @Transactional(readOnly = true)
    Page<AuthorRoleResponseDto> getAllRoles(Pageable pageable);

    @Transactional(readOnly = true)
    AuthorRoleResponseDto getRoleById(Long id);

    @Transactional
    void createRole(AuthorRoleRequestDto requestDto);

    @Transactional
    void updateRole(Long id, AuthorRoleRequestDto requestDto);

    @Transactional
    void deleteRole(Long id);

    @Transactional
    AuthorRole getOrCreateRole(String name);
}

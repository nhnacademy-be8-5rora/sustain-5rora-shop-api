package store.aurora.book.service.author.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.author.AuthorRoleRequestDto;
import store.aurora.book.dto.author.AuthorRoleResponseDto;
import store.aurora.book.entity.AuthorRole;
import store.aurora.book.exception.author.AuthorRoleAlreadyExistsException;
import store.aurora.book.exception.author.AuthorRoleLinkedToBooksException;
import store.aurora.book.exception.author.AuthorRoleNotFoundException;
import store.aurora.book.repository.author.AuthorRoleRepository;
import store.aurora.book.repository.author.BookAuthorRepository;
import store.aurora.book.service.author.AuthorRoleService;

@Service
@RequiredArgsConstructor
public class AuthorRoleServiceImpl implements AuthorRoleService {
    private final AuthorRoleRepository authorRoleRepository;
    private final BookAuthorRepository bookAuthorRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<AuthorRoleResponseDto> getAllRoles(Pageable pageable) {
        return authorRoleRepository.findAllByOrderById(pageable)
                .map(role -> new AuthorRoleResponseDto(role.getId(), role.getRole()));
    }

    @Transactional(readOnly = true)
    @Override
    public AuthorRoleResponseDto getRoleById(Long id) {
        AuthorRole role = findById(id);
        return new AuthorRoleResponseDto(role.getId(), role.getRole());
    }

    @Transactional
    @Override
    public void createRole(AuthorRoleRequestDto requestDto) {
        validateDuplicateName(requestDto.getRole());
        AuthorRole role = new AuthorRole();
        role.setRole(requestDto.getRole());
        authorRoleRepository.save(role);
    }

    @Transactional
    @Override
    public void updateRole(Long id, AuthorRoleRequestDto requestDto) {
        AuthorRole role = findById(id);
        validateDuplicateName(requestDto.getRole());
        role.setRole(requestDto.getRole());
        authorRoleRepository.save(role);
    }

    @Transactional
    @Override
    public void deleteRole(Long id) {
        if (!authorRoleRepository.existsById(id)) {
            throw new AuthorRoleNotFoundException("작가 역할을 찾을 수 없습니다. ID: " + id);
        }
        boolean isLinkedToBooks = bookAuthorRepository.existsByAuthorId(id);
        if (isLinkedToBooks) {
            throw new AuthorRoleLinkedToBooksException("작가 역할과 연결된 책이 있어 삭제할 수 없습니다. ID: " + id);
        }
        authorRoleRepository.deleteById(id);
    }

    @Transactional
    @Override
    public AuthorRole getOrCreateRole(String role) {
        return authorRoleRepository.findByRole(role)
                .orElseGet(() -> authorRoleRepository.save(new AuthorRole(null, role)));
    }

    private void validateDuplicateName(String role) {
        if (authorRoleRepository.existsByRole(role)) {
            throw new AuthorRoleAlreadyExistsException("이미 존재하는 작가 역할입니다: " + role);
        }
    }

    private AuthorRole findById(Long id) {
        return authorRoleRepository.findById(id)
                .orElseThrow(() -> new AuthorRoleNotFoundException("작가 역할을 찾을 수 없습니다. ID: " + id));
    }
}
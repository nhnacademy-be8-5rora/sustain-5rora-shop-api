package store.aurora.book.service.author.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.aurora.book.dto.author.AuthorRoleRequestDto;
import store.aurora.book.dto.author.AuthorRoleResponseDto;
import store.aurora.book.entity.AuthorRole;
import store.aurora.book.exception.author.AuthorRoleAlreadyExistsException;
import store.aurora.book.exception.author.AuthorRoleLinkedToBooksException;
import store.aurora.book.exception.author.AuthorRoleNotFoundException;
import store.aurora.book.repository.author.AuthorRoleRepository;
import store.aurora.book.repository.author.BookAuthorRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorRoleServiceImplTest {

    @InjectMocks
    private AuthorRoleServiceImpl authorRoleService;

    @Mock
    private AuthorRoleRepository authorRoleRepository;

    @Mock
    private BookAuthorRepository bookAuthorRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("모든 작가 역할을 페이지네이션으로 조회")
    void testGetAllRoles() {
        // Given
        Pageable pageable = mock(Pageable.class);
        List<AuthorRole> roles = List.of(new AuthorRole(1L, "작가"), new AuthorRole(2L, "편집자"));
        when(authorRoleRepository.findAllByOrderById(pageable)).thenReturn(new PageImpl<>(roles));

        // When
        Page<AuthorRoleResponseDto> result = authorRoleService.getAllRoles(pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getRole()).isEqualTo("작가");
    }

    @Test
    @DisplayName("ID로 작가 역할 조회")
    void testGetRoleById() {
        // Given
        AuthorRole role = new AuthorRole(1L, "작가");
        when(authorRoleRepository.findById(1L)).thenReturn(Optional.of(role));

        // When
        AuthorRoleResponseDto response = authorRoleService.getRoleById(1L);

        // Then
        assertThat(response.getRole()).isEqualTo("작가");
    }

    @Test
    @DisplayName("존재하지 않는 작가 역할 조회 시 예외 발생")
    void testGetRoleById_NotFound() {
        // Given
        when(authorRoleRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authorRoleService.getRoleById(1L))
                .isInstanceOf(AuthorRoleNotFoundException.class);
    }

    @Test
    @DisplayName("새로운 작가 역할 추가")
    void testCreateRole() {
        // Given
        AuthorRoleRequestDto request = new AuthorRoleRequestDto("번역가");
        when(authorRoleRepository.existsByRole(request.getRole())).thenReturn(false);

        // When
        authorRoleService.createRole(request);

        // Then
        verify(authorRoleRepository, times(1)).save(any(AuthorRole.class));
    }

    @Test
    @DisplayName("중복된 작가 역할 추가 시 예외 발생")
    void testCreateRole_Duplicate() {
        // Given
        AuthorRoleRequestDto request = new AuthorRoleRequestDto("편집자");
        when(authorRoleRepository.existsByRole(request.getRole())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authorRoleService.createRole(request))
                .isInstanceOf(AuthorRoleAlreadyExistsException.class);
    }

    @Test
    @DisplayName("작가 역할 수정")
    void testUpdateRole() {
        // Given
        AuthorRole existingRole = new AuthorRole(1L, "작가");
        AuthorRoleRequestDto updateRequest = new AuthorRoleRequestDto("수정된 역할");
        when(authorRoleRepository.findById(1L)).thenReturn(Optional.of(existingRole));
        when(authorRoleRepository.existsByRole(updateRequest.getRole())).thenReturn(false);

        // When
        authorRoleService.updateRole(1L, updateRequest);

        // Then
        assertThat(existingRole.getRole()).isEqualTo("수정된 역할");
    }

    @Test
    @DisplayName("작가 역할 삭제")
    void testDeleteRole() {
        // Given
        when(authorRoleRepository.existsById(1L)).thenReturn(true);
        when(bookAuthorRepository.existsByAuthorId(1L)).thenReturn(false);

        // When
        authorRoleService.deleteRole(1L);

        // Then
        verify(authorRoleRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 역할 삭제 시 예외 발생")
    void testDeleteAuthorRole_NotFound() {
        // Given
        when(authorRoleRepository.existsById(1L)).thenReturn(false); // 역할이 존재하지 않음

        // When & Then
        assertThatThrownBy(() -> authorRoleService.deleteRole(1L))
                .isInstanceOf(AuthorRoleNotFoundException.class)
                .hasMessageContaining("작가 역할을 찾을 수 없습니다. ID: 1");
    }

    @Test
    @DisplayName("연결된 책이 있는 작가 역할 삭제 시 예외 발생")
    void testDeleteRole_LinkedToBooks() {
        // Given
        when(authorRoleRepository.existsById(1L)).thenReturn(true);
        when(bookAuthorRepository.existsByAuthorId(1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authorRoleService.deleteRole(1L))
                .isInstanceOf(AuthorRoleLinkedToBooksException.class);
    }

    @Test
    @DisplayName("존재하는 작가 역할을 조회하면 새로운 객체를 생성하지 않고 반환해야 한다")
    void testGetOrCreateRole_Existing() {
        // Given
        String roleName = "작가";
        AuthorRole existingRole = new AuthorRole(1L, roleName);
        when(authorRoleRepository.findByRole(roleName)).thenReturn(Optional.of(existingRole));

        // When
        AuthorRole result = authorRoleService.getOrCreateRole(roleName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(roleName);
        verify(authorRoleRepository, times(1)).findByRole(roleName);
        verify(authorRoleRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 작가 역할을 조회하면 새로 생성하고 저장해야 한다")
    void testGetOrCreateRole_New() {
        // Given
        String roleName = "새로운 역할";
        when(authorRoleRepository.findByRole(roleName)).thenReturn(Optional.empty());
        when(authorRoleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        AuthorRole result = authorRoleService.getOrCreateRole(roleName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(roleName);
        verify(authorRoleRepository, times(1)).findByRole(roleName);
        verify(authorRoleRepository, times(1)).save(any(AuthorRole.class));
    }
}
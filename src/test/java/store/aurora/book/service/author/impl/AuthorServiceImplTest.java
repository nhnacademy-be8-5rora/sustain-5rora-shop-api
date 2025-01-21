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
import store.aurora.book.dto.author.AuthorRequestDto;
import store.aurora.book.dto.author.AuthorResponseDto;
import store.aurora.book.entity.Author;
import store.aurora.book.exception.author.AuthorAlreadyExistsException;
import store.aurora.book.exception.author.AuthorLinkedToBooksException;
import store.aurora.book.exception.author.AuthorNotFoundException;
import store.aurora.book.repository.author.AuthorRepository;
import store.aurora.book.repository.author.BookAuthorRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthorServiceImplTest {

    @InjectMocks
    private AuthorServiceImpl authorService;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookAuthorRepository bookAuthorRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("모든 작가를 페이지네이션으로 조회")
    void testGetAllAuthors() {
        // Given
        Pageable pageable = mock(Pageable.class);
        List<Author> authors = List.of(new Author(1L, "한강"), new Author(2L, "김영한"));
        when(authorRepository.findAll(pageable)).thenReturn(new PageImpl<>(authors));

        // When
        Page<AuthorResponseDto> result = authorService.getAllAuthors(pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("한강");
    }

    @Test
    @DisplayName("ID로 작가 조회")
    void testGetAuthorById() {
        // Given
        Author author = new Author(1L, "한강");
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        // When
        AuthorResponseDto response = authorService.getAuthorById(1L);

        // Then
        assertThat(response.getName()).isEqualTo("한강");
    }

    @Test
    @DisplayName("존재하지 않는 작가 조회 시 예외 발생")
    void testGetAuthorById_NotFound() {
        // Given
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authorService.getAuthorById(1L))
                .isInstanceOf(AuthorNotFoundException.class);
    }

    @Test
    @DisplayName("새로운 작가 추가")
    void testCreateAuthor() {
        // Given
        AuthorRequestDto request = new AuthorRequestDto("김영한");
        when(authorRepository.existsByName(request.getName())).thenReturn(false);
        when(authorRepository.save(any())).thenReturn(new Author(1L, request.getName()));

        // When
        authorService.createAuthor(request);

        // Then
        verify(authorRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("중복된 이름의 작가 추가 시 예외 발생")
    void testCreateAuthor_Duplicate() {
        // Given
        AuthorRequestDto request = new AuthorRequestDto("김영한");
        when(authorRepository.existsByName(request.getName())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authorService.createAuthor(request))
                .isInstanceOf(AuthorAlreadyExistsException.class);
    }

    @Test
    @DisplayName("작가 정보 수정")
    void testUpdateAuthor() {
        // Given
        Author existingAuthor = new Author(1L, "한강");
        AuthorRequestDto updateRequest = new AuthorRequestDto("변경된 작가");
        when(authorRepository.findById(1L)).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.existsByName(updateRequest.getName())).thenReturn(false);

        // When
        authorService.updateAuthor(1L, updateRequest);

        // Then
        assertThat(existingAuthor.getName()).isEqualTo("변경된 작가");
    }

    @Test
    @DisplayName("작가 삭제")
    void testDeleteAuthor() {
        // Given
        when(authorRepository.existsById(1L)).thenReturn(true);
        when(bookAuthorRepository.existsByAuthorId(1L)).thenReturn(false);

        // When
        authorService.deleteAuthor(1L);

        // Then
        verify(authorRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 작가 삭제 시 예외 발생")
    void testDeleteAuthor_NotFound() {
        // Given
        when(authorRepository.existsById(1L)).thenReturn(false); // 작가가 존재하지 않음

        // When & Then
        assertThatThrownBy(() -> authorService.deleteAuthor(1L))
                .isInstanceOf(AuthorNotFoundException.class)
                .hasMessageContaining("작가를 찾을 수 없습니다. ID: 1");
    }

    @Test
    @DisplayName("연결된 책이 있는 작가 삭제 시 예외 발생")
    void testDeleteAuthor_LinkedToBooks() {
        // Given
        when(authorRepository.existsById(1L)).thenReturn(true);
        when(bookAuthorRepository.existsByAuthorId(1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authorService.deleteAuthor(1L))
                .isInstanceOf(AuthorLinkedToBooksException.class);
    }

    @Test
    @DisplayName("존재하는 작가를 조회하면 새로운 객체를 생성하지 않고 반환")
    void testGetOrCreateAuthor_Existing() {
        // Given
        String authorName = "김영한";
        Author existingAuthor = new Author(1L, authorName);
        when(authorRepository.findByName(authorName)).thenReturn(Optional.of(existingAuthor));

        // When
        Author result = authorService.getOrCreateAuthor(authorName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(authorName);
        verify(authorRepository, times(1)).findByName(authorName);
        verify(authorRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 작가를 조회하면 새로 생성하고 저장해야 한다")
    void testGetOrCreateAuthor_New() {
        // Given
        String authorName = "새로운 작가";
        when(authorRepository.findByName(authorName)).thenReturn(Optional.empty());
        when(authorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0)); // 저장된 객체 반환

        // When
        Author result = authorService.getOrCreateAuthor(authorName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(authorName);
        verify(authorRepository, times(1)).findByName(authorName);
        verify(authorRepository, times(1)).save(any(Author.class));
    }
}
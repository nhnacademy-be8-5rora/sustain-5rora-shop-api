package store.aurora.book.service.author;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.aurora.book.dto.author.AuthorRequestDto;
import store.aurora.book.dto.author.AuthorResponseDto;
import store.aurora.book.entity.Author;

public interface AuthorService {
    Page<AuthorResponseDto> getAllAuthors(Pageable pageable);
    AuthorResponseDto getAuthorById(Long id);
    void createAuthor(AuthorRequestDto requestDto);
    void updateAuthor(Long id, AuthorRequestDto requestDto);
    void deleteAuthor(Long id);
    Author getOrCreateAuthor(String name);
}

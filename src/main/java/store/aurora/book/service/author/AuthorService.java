package store.aurora.book.service.author;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.author.AuthorRequestDto;
import store.aurora.book.dto.author.AuthorResponseDto;
import store.aurora.book.entity.Author;

public interface AuthorService {

    @Transactional(readOnly = true)
    Page<AuthorResponseDto> getAllAuthors(Pageable pageable);

    @Transactional(readOnly = true)
    AuthorResponseDto getAuthorById(Long id);

    @Transactional
    void createAuthor(AuthorRequestDto requestDto);

    @Transactional
    void updateAuthor(Long id, AuthorRequestDto requestDto);

    @Transactional
    void deleteAuthor(Long id);

    @Transactional
    Author getOrCreateAuthor(String name);
}

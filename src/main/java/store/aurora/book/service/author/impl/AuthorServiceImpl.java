package store.aurora.book.service.author.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.author.AuthorRequestDto;
import store.aurora.book.dto.author.AuthorResponseDto;
import store.aurora.book.entity.Author;
import store.aurora.book.exception.author.AuthorAlreadyExistsException;
import store.aurora.book.exception.author.AuthorLinkedToBooksException;
import store.aurora.book.exception.author.AuthorNotFoundException;
import store.aurora.book.repository.author.AuthorRepository;
import store.aurora.book.repository.author.BookAuthorRepository;
import store.aurora.book.service.author.AuthorService;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final BookAuthorRepository bookAuthorRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<AuthorResponseDto> getAllAuthors(Pageable pageable) {
        return authorRepository.findAll(pageable)
                .map(author -> new AuthorResponseDto(author.getId(), author.getName()));
    }

    @Transactional(readOnly = true)
    @Override
    public AuthorResponseDto getAuthorById(Long id) {
        Author author = findById(id);
        return new AuthorResponseDto(author.getId(), author.getName());
    }

    @Transactional
    @Override
    public void createAuthor(AuthorRequestDto requestDto) {
        validateDuplicateName(requestDto.getName());
        Author author = new Author(requestDto.getName());
        authorRepository.save(author);
    }

    @Transactional
    @Override
    public void updateAuthor(Long id, AuthorRequestDto requestDto) {
        Author author = findById(id);
        validateDuplicateName(requestDto.getName());
        author.setName(requestDto.getName());
        authorRepository.save(author);
    }

    @Transactional
    @Override
    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new AuthorNotFoundException("작가를 찾을 수 없습니다. ID: " + id);
        }

        // 연결된 책이 있는지 확인
        boolean isLinkedToBooks = bookAuthorRepository.existsByAuthorId(id);
        if (isLinkedToBooks) {
            throw new AuthorLinkedToBooksException("작가와 연결된 책이 있어 삭제할 수 없습니다. ID: " + id);
        }

        authorRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Author getOrCreateAuthor(String name) {
        return authorRepository.findByName(name)
                .orElseGet(() -> authorRepository.save(new Author(name)));
    }

    // 중복된 이름 예외 처리
    private void validateDuplicateName(String name) {
        if (authorRepository.existsByName(name)) {
            throw new AuthorAlreadyExistsException("이미 존재하는 작가입니다: " + name);
        }
    }

    // ID로 작가 조회
    private Author findById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("작가를 찾을 수 없습니다. ID: " + id));
    }
}

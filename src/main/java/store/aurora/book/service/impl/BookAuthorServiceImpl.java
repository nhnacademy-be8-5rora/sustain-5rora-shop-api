package store.aurora.book.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.entity.Author;
import store.aurora.book.entity.AuthorRole;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookAuthor;
import store.aurora.book.repository.AuthorRepository;
import store.aurora.book.repository.AuthorRoleRepository;
import store.aurora.book.repository.BookAuthorRepository;
import store.aurora.book.service.BookAuthorService;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookAuthorServiceImpl implements BookAuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorRoleRepository authorRoleRepository;
    private final BookAuthorRepository bookAuthorRepository;

    @Transactional
    @Override
    public void parseAndSaveBookAuthors(Book book, String authorsString) {
        List<BookAuthor> bookAuthors = parseAuthors(book, authorsString);
        bookAuthorRepository.saveAll(bookAuthors);
    }

    @Transactional
    @Override
    public void addAuthorToBook(List<BookAuthor> bookAuthors, Book book, String authorName, String role) {
        Author author = authorRepository.findByName(authorName)
                .orElseGet(() -> authorRepository.save(new Author(null, authorName)));

        AuthorRole authorRole = authorRoleRepository.findByRole(role)
                .orElseGet(() -> authorRoleRepository.save(new AuthorRole(null, role)));

        BookAuthor bookAuthor = new BookAuthor();
        bookAuthor.setBook(book);
        bookAuthor.setAuthor(author);
        bookAuthor.setAuthorRole(authorRole);

        bookAuthors.add(bookAuthor);
    }

    @Transactional
    @Override
    public void deleteAuthorsByBook(Book book) {
        List<BookAuthor> bookAuthors = bookAuthorRepository.findByBook(book);
        bookAuthorRepository.deleteAll(bookAuthors);
    }

    @Override
    public String getFormattedAuthors(Book book) {
        List<BookAuthor> bookAuthors = bookAuthorRepository.findByBook(book);

        // 역할별로 작가를 그룹화
        Map<String, List<String>> groupedAuthors = new LinkedHashMap<>();
        for (BookAuthor bookAuthor : bookAuthors) {
            // 역할의 한글 이름 가져오기
            String role = bookAuthor.getAuthorRole() != null
                    ? bookAuthor.getAuthorRole().getRole()
                    : "역할 없음";

            String authorName = bookAuthor.getAuthor().getName();
            groupedAuthors.computeIfAbsent(role, k -> new ArrayList<>()).add(authorName);
        }

        // 역할별로 작가를 묶어서 문자열로 포맷
        return groupedAuthors.entrySet().stream()
                .map(entry -> {
                    String authors = String.join(", ", entry.getValue());
                    return entry.getKey().equals("역할 없음") ? authors : authors + " (" + entry.getKey() + ")";
                })
                .collect(Collectors.joining(", "));
    }

    private void validateAuthorsString(String authorsString) {
        // 입력 형식을 검증하는 정규식 (작가 이름과 역할)
        String regex = "^(\\s*[가-힣a-zA-Z0-9]+\\s*(\\(\\s*[가-힣a-zA-Z0-9]+\\s*\\))?\\s*,\\s*)*(\\s*[가-힣a-zA-Z0-9]+\\s*(\\(\\s*[가-힣a-zA-Z0-9]+\\s*\\))?\\s*)$";

        if (!authorsString.matches(regex)) {
            throw new IllegalArgumentException("작가 입력 형식이 잘못되었습니다. 형식: '작가1, 작가2 (역할1), 작가3 (역할2)'");
        }
    }

    private List<BookAuthor> parseAuthors(Book book, String authorsString) {
        List<BookAuthor> bookAuthors = new ArrayList<>();
        String[] entries = authorsString.split(", ");
        String currentRole = null;

        List<String> pendingAuthors = new ArrayList<>();

        for (String entry : entries) {
            if (entry.contains("(") && entry.contains(")")) {
                int roleStart = entry.lastIndexOf("(");
                int roleEnd = entry.lastIndexOf(")");
                currentRole = entry.substring(roleStart + 1, roleEnd).trim();

                String[] names = entry.substring(0, roleStart).split(", ");
                pendingAuthors.addAll(Arrays.asList(names));

                for (String authorName : pendingAuthors) {
                    addAuthorToBook(bookAuthors, book, authorName.trim(), currentRole);
                }
                pendingAuthors.clear();
            } else {
                pendingAuthors.add(entry.trim());
            }
        }
        return bookAuthors;
    }


}

package store.aurora.book.service.author.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.entity.Author;
import store.aurora.book.entity.AuthorRole;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookAuthor;
import store.aurora.book.repository.author.AuthorRepository;
import store.aurora.book.repository.author.AuthorRoleRepository;
import store.aurora.book.repository.author.BookAuthorRepository;
import store.aurora.book.service.author.BookAuthorService;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookAuthorServiceImpl implements BookAuthorService {
    private static final String DEFAULT_ROLE = "지은이";

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
                    : DEFAULT_ROLE;

            String authorName = bookAuthor.getAuthor().getName();
            groupedAuthors.computeIfAbsent(role, k -> new ArrayList<>()).add(authorName);
        }

        // 역할별로 작가를 묶어서 문자열로 포맷
        return groupedAuthors.entrySet().stream()
                .map(entry -> {
                    String authors = String.join(", ", entry.getValue());
                    return entry.getKey().equals(DEFAULT_ROLE) ? authors : authors + " (" + entry.getKey() + ")";
                })
                .collect(Collectors.joining(", "));
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

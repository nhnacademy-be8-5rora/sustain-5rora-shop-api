package store.aurora.book.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    /**
     * 책과 연결된 작가 및 역할 정보를 파싱하고 저장합니다.
     * @param book 연결할 책 엔티티
     * @param authorsString 파싱할 작가 정보 문자열
     */
    @Override
    public void parseAndSaveBookAuthors(Book book, String authorsString) {
        List<BookAuthor> bookAuthors = parseAuthors(book, authorsString);
        bookAuthorRepository.saveAll(bookAuthors);
    }

    /**
     * 작가 문자열을 파싱하여 BookAuthor 엔티티 리스트를 생성합니다.
     * @param book 연결할 책 엔티티
     * @param authorsString 파싱할 작가 정보 문자열
     * @return 생성된 BookAuthor 리스트
     */
    @Override
    public List<BookAuthor> parseAuthors(Book book, String authorsString) {
        List<BookAuthor> bookAuthors = new ArrayList<>();
        String[] entries = authorsString.split(", ");
        AuthorRole.Role currentRole = null; // 기본 역할 없음

        // 리스트로 작가 이름을 누적하여 역할 적용
        List<String> pendingAuthors = new ArrayList<>();

        for (String entry : entries) {
            if (entry.contains("(") && entry.contains(")")) {
                // 괄호가 있는 경우 역할 추출
                int roleStart = entry.lastIndexOf("(");
                int roleEnd = entry.lastIndexOf(")");
                String roleString = entry.substring(roleStart + 1, roleEnd).trim();

                // Enum에서 역할 찾기
                currentRole = Arrays.stream(AuthorRole.Role.values())
                        .filter(r -> r.getKoreanName().equals(roleString))
                        .findFirst()
                        .orElse(null);

                // 괄호 앞의 이름들에 역할 적용
                String[] names = entry.substring(0, roleStart).split(", ");
                pendingAuthors.addAll(Arrays.asList(names));
                // 현재 역할로 누적된 모든 작가 연결
                for (String authorName : pendingAuthors) {
                    addAuthorToBook(bookAuthors, book, authorName.trim(), currentRole);
                }
                // 누적 리스트 초기화
                pendingAuthors.clear();
            } else {
                // 괄호가 없는 경우 이름 누적
                pendingAuthors.add(entry.trim());
            }
        }

        return bookAuthors;
    }

    /**
     * 주어진 책과 작가 정보를 BookAuthor로 연결합니다.
     * @param bookAuthors BookAuthor 리스트에 추가할 대상
     * @param book 연결할 책 엔티티
     * @param authorName 작가 이름
     * @param role 연결할 역할 (nullable)
     */
    @Override
    public void addAuthorToBook(List<BookAuthor> bookAuthors, Book book, String authorName, AuthorRole.Role role) {
        // 작가 저장 또는 가져오기
        Author author = authorRepository.findByName(authorName)
                .orElseGet(() -> authorRepository.save(new Author(null, authorName)));

        // 역할 가져오기 (nullable 허용)
        AuthorRole authorRole = role != null ? authorRoleRepository.findByRole(role)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + role)) : null;

        // BookAuthor 객체 생성 및 추가
        BookAuthor bookAuthor = new BookAuthor();
        bookAuthor.setBook(book);
        bookAuthor.setAuthor(author);
        bookAuthor.setAuthorRole(authorRole);

        bookAuthors.add(bookAuthor);
    }

    @Override
    public String getFormattedAuthors(Book book) {
        List<BookAuthor> bookAuthors = bookAuthorRepository.findByBook(book);

        // 역할별로 작가를 그룹화
        Map<String, List<String>> groupedAuthors = new LinkedHashMap<>();
        for (BookAuthor bookAuthor : bookAuthors) {
            // 역할의 한글 이름 가져오기
            String role = bookAuthor.getAuthorRole() != null
                    ? bookAuthor.getAuthorRole().getRole().getKoreanName() // 한글 역할명
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
}

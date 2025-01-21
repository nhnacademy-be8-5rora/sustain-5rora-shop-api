package store.aurora.book.service.author.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import store.aurora.book.dto.author.ParsedAuthorDto;
import store.aurora.book.entity.Author;
import store.aurora.book.entity.AuthorRole;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookAuthor;
import store.aurora.book.parser.AuthorParser;
import store.aurora.book.repository.author.BookAuthorRepository;
import store.aurora.book.service.author.AuthorRoleService;
import store.aurora.book.service.author.AuthorService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BookAuthorServiceImplTest {

    @InjectMocks
    private BookAuthorServiceImpl bookAuthorService;

    @Mock
    private AuthorService authorService;

    @Mock
    private AuthorRoleService authorRoleService;

    @Mock
    private BookAuthorRepository bookAuthorRepository;

    @Mock
    private AuthorParser authorParser;

    private Book testBook;
    private ParsedAuthorDto parsedAuthor1, parsedAuthor2;

    @BeforeEach
    void setUp() {
        testBook = new Book(); // 책 객체 생성
        testBook.setId(1L);
        testBook.setTitle("테스트 책");

        parsedAuthor1 = new ParsedAuthorDto("작가1", "지은이");
        parsedAuthor2 = new ParsedAuthorDto("작가2", "옮긴이");
    }

    @Test
    @DisplayName("저자 문자열을 파싱하여 BookAuthor 저장")
    void testParseAndSaveBookAuthors() {
        // Given
        String authorsString = "작가1(지은이), 작가2(옮긴이)";
        List<ParsedAuthorDto> parsedAuthors = List.of(parsedAuthor1, parsedAuthor2);

        Author author1 = new Author(1L, "작가1");
        Author author2 = new Author(2L, "작가2");

        AuthorRole role1 = new AuthorRole(1L, "지은이");
        AuthorRole role2 = new AuthorRole(2L, "옮긴이");

        when(authorParser.parseAuthors(authorsString)).thenReturn(parsedAuthors);
        when(authorService.getOrCreateAuthor("작가1")).thenReturn(author1);
        when(authorService.getOrCreateAuthor("작가2")).thenReturn(author2);
        when(authorRoleService.getOrCreateRole("지은이")).thenReturn(role1);
        when(authorRoleService.getOrCreateRole("옮긴이")).thenReturn(role2);

        // When
        bookAuthorService.parseAndSaveBookAuthors(testBook, authorsString);

        // Then
        verify(authorParser, times(1)).parseAuthors(authorsString);
        verify(authorService, times(1)).getOrCreateAuthor("작가1");
        verify(authorService, times(1)).getOrCreateAuthor("작가2");
        verify(authorRoleService, times(1)).getOrCreateRole("지은이");
        verify(authorRoleService, times(1)).getOrCreateRole("옮긴이");
        verify(bookAuthorRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("책의 저자 정보를 포맷팅된 문자열로 반환")
    void testGetFormattedAuthors() {
        // Given
        Author author1 = new Author(1L, "작가1");
        AuthorRole role1 = new AuthorRole(1L, "지은이");

        Author author2 = new Author(2L, "작가2");
        AuthorRole role2 = new AuthorRole(2L, "옮긴이");

        BookAuthor bookAuthor1 = new BookAuthor(1L, author1, role1, testBook);
        BookAuthor bookAuthor2 = new BookAuthor(2L, author2, role2, testBook);

        List<BookAuthor> bookAuthors = List.of(bookAuthor1, bookAuthor2);
        String formattedAuthors = "작가1(지은이), 작가2(옮긴이)";

        when(bookAuthorRepository.findByBook(testBook)).thenReturn(bookAuthors);
        when(authorParser.formatAuthors(bookAuthors)).thenReturn(formattedAuthors);

        // When
        String result = bookAuthorService.getFormattedAuthors(testBook);

        // Then
        assertThat(result).isEqualTo(formattedAuthors);
        verify(bookAuthorRepository, times(1)).findByBook(testBook);
        verify(authorParser, times(1)).formatAuthors(bookAuthors);
    }
}
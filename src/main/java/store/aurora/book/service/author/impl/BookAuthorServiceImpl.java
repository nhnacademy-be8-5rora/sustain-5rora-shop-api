package store.aurora.book.service.author.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.author.ParsedAuthorDto;
import store.aurora.book.entity.Author;
import store.aurora.book.entity.AuthorRole;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookAuthor;
import store.aurora.book.parser.AuthorParser;
import store.aurora.book.repository.author.BookAuthorRepository;
import store.aurora.book.service.author.AuthorRoleService;
import store.aurora.book.service.author.AuthorService;
import store.aurora.book.service.author.BookAuthorService;
import java.util.*;


@Service
@RequiredArgsConstructor
public class BookAuthorServiceImpl implements BookAuthorService {

    private final AuthorService authorService;
    private final AuthorRoleService authorRoleService;
    private final BookAuthorRepository bookAuthorRepository;
    private final AuthorParser authorParser;

    @Transactional
    @Override
    public void parseAndSaveBookAuthors(Book book, String authorsString) {
        List<ParsedAuthorDto> parsedAuthors = authorParser.parseAuthors(authorsString);
        List<BookAuthor> bookAuthors = new ArrayList<>();

        for (ParsedAuthorDto parsedAuthor : parsedAuthors) {
            bookAuthors.add(createBookAuthor(book, parsedAuthor.getName(), parsedAuthor.getRole()));
        }

        bookAuthorRepository.saveAll(bookAuthors);
    }

    private BookAuthor createBookAuthor(Book book, String authorName, String role) {
        Author author = authorService.getOrCreateAuthor(authorName);
        AuthorRole authorRole = authorRoleService.getOrCreateRole(role);
        return new BookAuthor(null, author, authorRole, book);
    }

    @Override
    public String getFormattedAuthors(Book book) {
        List<BookAuthor> bookAuthors = bookAuthorRepository.findByBook(book);
        return authorParser.formatAuthors(bookAuthors);
    }


}

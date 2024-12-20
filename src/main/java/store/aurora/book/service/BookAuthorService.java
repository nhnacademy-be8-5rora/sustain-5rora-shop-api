package store.aurora.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.entity.Author;
import store.aurora.book.entity.AuthorRole;
import store.aurora.book.entity.BookAuthor;
import store.aurora.book.repository.BookAuthorRepository;

import java.util.List;

public interface BookAuthorService {
    BookAuthor addAuthorToBook(Long bookId, Author author, AuthorRole authorRole);
    void removeAuthorFromBook(Long bookAuthorId);
    List<BookAuthor> addAuthorsToBook(Long bookId, List<BookAuthor> bookAuthors);

}

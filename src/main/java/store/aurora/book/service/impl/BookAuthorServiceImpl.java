package store.aurora.book.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookAuthor;
import store.aurora.book.entity.Author;
import store.aurora.book.entity.AuthorRole;
import store.aurora.book.repository.BookAuthorRepository;
import store.aurora.book.repository.BookRepository;
import store.aurora.book.service.BookAuthorService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BookAuthorServiceImpl implements BookAuthorService {

    private final BookAuthorRepository bookAuthorRepository;
    private final BookRepository bookRepository;

    @Override
    public BookAuthor addAuthorToBook(Long bookId, Author author, AuthorRole authorRole) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        BookAuthor bookAuthor = new BookAuthor();
        bookAuthor.setBook(book);
        bookAuthor.setAuthor(author);
        bookAuthor.setAuthorRole(authorRole);

        return bookAuthorRepository.save(bookAuthor);
    }

    @Override
    public void removeAuthorFromBook(Long bookAuthorId) {
        bookAuthorRepository.deleteById(bookAuthorId);
    }

    @Override
    public List<BookAuthor> addAuthorsToBook(Long bookId, List<BookAuthor> bookAuthors) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        for (BookAuthor bookAuthor : bookAuthors) {
            bookAuthor.setBook(book);
        }

        return bookAuthorRepository.saveAll(bookAuthors);
    }
}


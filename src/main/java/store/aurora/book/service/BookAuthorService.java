package store.aurora.book.service;


import store.aurora.book.entity.AuthorRole;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookAuthor;

import java.util.List;

public interface BookAuthorService {
    void parseAndSaveBookAuthors(Book book, String authorsString);
    void addAuthorToBook(List<BookAuthor> bookAuthors, Book book, String authorName, String roleName);

    void deleteAuthorsByBook(Book book);

    String getFormattedAuthors(Book book);
}

package store.aurora.book.service.author;

import store.aurora.book.entity.Book;

public interface BookAuthorService {
    void parseAndSaveBookAuthors(Book book, String authorsString);
    String getFormattedAuthors(Book book);
}

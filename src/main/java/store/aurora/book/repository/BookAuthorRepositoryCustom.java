package store.aurora.book.repository;

import store.aurora.document.AuthorDocument;

import java.util.List;

public interface BookAuthorRepositoryCustom {
    List<AuthorDocument> findAuthorsByBookId(Long bookId);
}


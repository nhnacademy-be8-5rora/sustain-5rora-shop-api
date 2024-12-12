package store.aurora.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.BookView;

public interface BookViewRepository extends JpaRepository<BookView, Long> {}


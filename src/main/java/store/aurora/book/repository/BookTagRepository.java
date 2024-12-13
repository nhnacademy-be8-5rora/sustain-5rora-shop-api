package store.aurora.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.tag.BookTag;

public interface BookTagRepository extends JpaRepository<BookTag, Long> {}


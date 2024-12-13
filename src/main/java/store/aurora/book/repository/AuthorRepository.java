package store.aurora.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {}

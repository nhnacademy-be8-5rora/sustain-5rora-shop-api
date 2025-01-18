package store.aurora.book.repository.author;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.Author;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    boolean existsByName(String name);
    Optional<Author> findByName(String name);

}

package store.aurora.book.repository.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.aurora.book.entity.tag.Tag;

import java.util.Optional;


public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

}


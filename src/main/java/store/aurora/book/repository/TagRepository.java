package store.aurora.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {}


package store.aurora.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.Like;

public interface LikeRepository extends JpaRepository<Like, String> {}


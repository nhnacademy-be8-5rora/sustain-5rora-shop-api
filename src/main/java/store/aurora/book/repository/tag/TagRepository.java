package store.aurora.book.repository.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import store.aurora.book.entity.tag.Tag;

import java.util.List;
import java.util.Optional;


public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
    @Query("SELECT t FROM Tag t WHERE t.name LIKE %:keyword%")
    List<Tag> findByKeyword(@Param("keyword") String keyword);

}


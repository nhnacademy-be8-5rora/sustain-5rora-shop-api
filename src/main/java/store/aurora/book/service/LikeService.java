package store.aurora.book.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.aurora.book.entity.Like;
import store.aurora.search.dto.BookSearchResponseDTO;

import java.util.List;
import java.util.Set;

public interface LikeService {
    boolean pressLike(Long bookId, String userId);

    Like cancelLike(Long bookId, String userId);

    Set<Long> getLikedBookIds(String userId, List<Long> bookIds);
}

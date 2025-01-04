package store.aurora.book.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.aurora.book.entity.Like;
import store.aurora.search.dto.BookSearchResponseDTO;

import java.util.List;

public interface LikeService {
    boolean pressLike(Long bookId, String userId);

    Like cancelLike(Long bookId, String userId);

    boolean isLiked(String userId, Long bookId);

}

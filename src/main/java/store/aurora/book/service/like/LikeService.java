package store.aurora.book.service.like;


import store.aurora.book.entity.Like;

import java.util.List;
import java.util.Set;

public interface LikeService {
    boolean pressLike(Long bookId, String userId);

    Like cancelLike(Long bookId, String userId);

    Set<Long> getLikedBookIds(String userId, List<Long> bookIds);
}

package store.aurora.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.service.like.LikeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class LikeController {

    private final LikeService likeService;


    // merge
    @PostMapping("/likes/{bookId}")
    public ResponseEntity<Boolean> doLike(@PathVariable Long bookId,
                                          @RequestHeader(value = "X-USER-ID") String userId) {
        // 서버에서 좋아요를 처리하고, 성공 여부를 boolean 값으로 반환
        boolean isLiked = likeService.pressLike(bookId, userId);
        return ResponseEntity.ok(isLiked);  // 좋아요 상태를 true/false로 응답
    }



}

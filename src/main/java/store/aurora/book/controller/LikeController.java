package store.aurora.book.controller;

import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.LikeDto;
import store.aurora.book.service.LikeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class LikeController {

    private final LikeService likeService;


    // merge
    @PostMapping("/likes/{bookId}")
    public ResponseEntity<LikeDto> doLike(@PathVariable Long bookId,
                                          @RequestHeader(value = "X-USER-ID") String userId) {
        likeService.pressLike(bookId, userId);
        return ResponseEntity.ok().build();
    }



}

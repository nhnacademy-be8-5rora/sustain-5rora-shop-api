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
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;


    @PostMapping("/books/{bookId}")
    public ResponseEntity<LikeDto> doLike(@PathVariable Long bookId,
                                          @RequestHeader(name = "X-USER-ID") String userId) {
        return ResponseEntity.ok().build();
    }

}

package store.aurora.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.aurora.user.entity.UserRank;
import store.aurora.user.service.impl.UserRankService;

import java.util.List;

@RestController
@RequestMapping("/api/user-ranks")
@RequiredArgsConstructor
public class UserRankController {
    private final UserRankService userRankService;

    @GetMapping
    public ResponseEntity<List<UserRank>> getAllUserRanks() {
        List<UserRank> userRanks = userRankService.getAllUserRanks();
        return ResponseEntity.ok(userRanks);
    }
}
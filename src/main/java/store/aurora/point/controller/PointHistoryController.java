package store.aurora.point.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.point.dto.PointHistoryResponse;
import store.aurora.point.service.PointHistoryService;
import store.aurora.point.service.PointSpendService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points/history")
public class PointHistoryController {

    private final PointHistoryService pointHistoryService;
    private final PointSpendService pointSpendService;

    @GetMapping
    public ResponseEntity<Page<PointHistoryResponse>> getPointHistory(
            @RequestHeader(value = "X-USER-ID") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PointHistoryResponse> pointHistories = pointHistoryService.getPointHistoryByUser(userId, page, size);
        return ResponseEntity.ok(pointHistories);
    }

    @GetMapping("/available")
    public ResponseEntity<Integer> getAvailablePoints(@RequestHeader(value = "X-USER-ID") String userId) {
        Integer availablePoints = pointSpendService.getAvailablePointsByUser(userId);
        return ResponseEntity.ok(availablePoints);
    }
}
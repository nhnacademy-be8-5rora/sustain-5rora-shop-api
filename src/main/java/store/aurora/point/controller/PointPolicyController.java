package store.aurora.point.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import store.aurora.point.dto.PointPolicyRequest;
import store.aurora.point.dto.PointPolicyUpdateRequest;
import store.aurora.point.entity.PointPolicy;
import store.aurora.point.service.PointPolicyService;

import java.util.List;

@RestController
@RequestMapping("/api/points/policies")
public class PointPolicyController {

    private final PointPolicyService pointPolicyService;

    @Autowired
    public PointPolicyController(PointPolicyService pointPolicyService) {
        this.pointPolicyService = pointPolicyService;
    }

    @GetMapping
    public List<PointPolicy> getAllPointPolicies() {
        return pointPolicyService.getAllPointPolicies();
    }

    @PatchMapping("/{id}")
    public void updatePointPolicy(
            @PathVariable Integer id,
            @RequestBody PointPolicyUpdateRequest request) {
        pointPolicyService.updatePointPolicyValue(id, request.getPointPolicyValue());
    }

    @Operation(summary = "포인트 정책 등록", description = "새로운 포인트 정책을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "정책이 성공적으로 생성됨",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PointPolicy.class))),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패 또는 잘못된 요청 데이터 (Enum 값 등)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "중복된 포인트 정책 이름",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PointPolicy> createPointPolicy(@RequestBody @Valid PointPolicyRequest request) {
        PointPolicy pointPolicy = new PointPolicy(
                request.getPointPolicyName(),
                request.getPointPolicyType(),
                request.getPointPolicyValue()
        );
        PointPolicy savedPolicy = pointPolicyService.createPointPolicy(pointPolicy);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPolicy);
    }
}
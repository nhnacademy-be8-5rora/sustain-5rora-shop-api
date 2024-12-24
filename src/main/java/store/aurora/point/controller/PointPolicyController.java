package store.aurora.point.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import store.aurora.point.dto.PointPolicyUpdateRequest;
import store.aurora.point.entity.PointPolicy;
import store.aurora.point.service.PointPolicyService;

import java.util.List;

@RestController
@RequestMapping("/api/point-policies")
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
}
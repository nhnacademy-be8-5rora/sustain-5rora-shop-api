package store.aurora.point.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.point.entity.PointPolicy;
import store.aurora.point.exception.PointPolicyAlreadyExistsException;
import store.aurora.point.exception.PointPolicyNotFoundException;
import store.aurora.point.repository.PointPolicyRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PointPolicyService {

    private final PointPolicyRepository pointPolicyRepository;

    @Autowired
    public PointPolicyService(PointPolicyRepository pointPolicyRepository) {
        this.pointPolicyRepository = pointPolicyRepository;
    }

    public List<PointPolicy> getAllPointPolicies() {
        return pointPolicyRepository.findAll();
    }

    public PointPolicy getPointPolicyById(Integer id) {
        return pointPolicyRepository.findById(id)
                .orElseThrow(() -> new PointPolicyNotFoundException(id));
    }

    @Transactional
    public void updatePointPolicyValue(Integer id, BigDecimal newValue) {
        getPointPolicyById(id).setPointPolicyValue(newValue);
    }

    @Transactional
    public void toggleStatus(Integer id) {
        PointPolicy pointPolicy = getPointPolicyById(id);
        pointPolicy.setIsActive(!pointPolicy.getIsActive()); // 활성화 상태 토글
    }

    public PointPolicy createPointPolicy(PointPolicy pointPolicy) {
        if (pointPolicyRepository.existsByPointPolicyName(pointPolicy.getPointPolicyName())) {
            throw new PointPolicyAlreadyExistsException(pointPolicy.getPointPolicyName());
        }

        if (pointPolicyRepository.existsById(pointPolicy.getId())) {
            throw new PointPolicyAlreadyExistsException(pointPolicy.getId().toString());
        }

        return pointPolicyRepository.save(pointPolicy);
    }
}
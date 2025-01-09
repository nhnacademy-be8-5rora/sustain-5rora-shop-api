package store.aurora.point.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import store.aurora.point.entity.PointPolicy;
import store.aurora.point.entity.PointPolicyType;
import store.aurora.point.exception.PointPolicyAlreadyExistsException;
import store.aurora.point.exception.PointPolicyNotFoundException;
import store.aurora.point.repository.PointPolicyRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointPolicyServiceTest {

    @InjectMocks
    private PointPolicyService pointPolicyService;

    @Mock
    private PointPolicyRepository pointPolicyRepository;

    // Given
    Integer policyId = 1;
    PointPolicy policy = new PointPolicy("Policy1", PointPolicyType.PERCENTAGE, BigDecimal.valueOf(10.0));

    @Test
    @DisplayName("getAllPointPolicies: Should return all point policies")
    void testGetAllPointPolicies() {
        // Given
        PointPolicy policy2 = new PointPolicy("Policy2", PointPolicyType.PERCENTAGE, BigDecimal.valueOf(5.0));
        when(pointPolicyRepository.findAll()).thenReturn(List.of(policy, policy2));

        // When
        List<PointPolicy> result = pointPolicyService.getAllPointPolicies();

        // Then
        assertThat(result)
                        .hasSize(2)
                        .containsExactly(policy, policy2);
        verify(pointPolicyRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getPointPolicyById: Should return the point policy if it exists")
    void testGetPointPolicyById_Exists() {
        // Given
        when(pointPolicyRepository.findById(policyId)).thenReturn(Optional.of(policy));

        // When
        PointPolicy result = pointPolicyService.getPointPolicyById(policyId);

        // Then
        assertThat(result).isEqualTo(policy);
        verify(pointPolicyRepository, times(1)).findById(policyId);
    }

    @Test
    @DisplayName("getPointPolicyById: Should throw exception if policy does not exist")
    void testGetPointPolicyById_NotExists() {
        when(pointPolicyRepository.findById(policyId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> pointPolicyService.getPointPolicyById(policyId))
                .isInstanceOf(PointPolicyNotFoundException.class)
                .hasMessageContaining(policyId.toString());
        verify(pointPolicyRepository, times(1)).findById(policyId);
    }

    @Test
    @DisplayName("updatePointPolicyValue: Should update the policy value")
    void testUpdatePointPolicyValue() {
        // Given
        BigDecimal newValue = BigDecimal.valueOf(20.0);
        when(pointPolicyRepository.findById(policyId)).thenReturn(Optional.of(policy));

        // When
        pointPolicyService.updatePointPolicyValue(policyId, newValue);

        // Then
        assertThat(policy.getPointPolicyValue()).isEqualTo(newValue);
        verify(pointPolicyRepository, times(1)).findById(policyId);
    }

    @Test
    @DisplayName("toggleStatus: Should toggle the policy status")
    void testToggleStatus() {
        // Given
        when(pointPolicyRepository.findById(policyId)).thenReturn(Optional.of(policy));

        // When
        pointPolicyService.toggleStatus(policyId);

        // Then
        assertThat(policy.getIsActive()).isFalse();
        verify(pointPolicyRepository, times(1)).findById(policyId);
    }

    @Test
    @DisplayName("createPointPolicy: Should create a new point policy")
    void testCreatePointPolicy_Success() {
        // Given
        when(pointPolicyRepository.existsByPointPolicyName(policy.getPointPolicyName())).thenReturn(false);
        when(pointPolicyRepository.save(policy)).thenReturn(policy);

        // When
        PointPolicy result = pointPolicyService.createPointPolicy(policy);

        // Then
        assertThat(result).isEqualTo(policy);
        verify(pointPolicyRepository, times(1)).existsByPointPolicyName(policy.getPointPolicyName());
        verify(pointPolicyRepository, times(1)).save(policy);
    }

    @Test
    @DisplayName("createPointPolicy: Should throw exception if policy name already exists")
    void testCreatePointPolicy_AlreadyExists() {
        // Given
        when(pointPolicyRepository.existsByPointPolicyName(policy.getPointPolicyName())).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> pointPolicyService.createPointPolicy(policy))
                .isInstanceOf(PointPolicyAlreadyExistsException.class)
                .hasMessageContaining(policy.getPointPolicyName());
        verify(pointPolicyRepository, times(1)).existsByPointPolicyName(policy.getPointPolicyName());
        verify(pointPolicyRepository, times(0)).save(policy);
    }
}
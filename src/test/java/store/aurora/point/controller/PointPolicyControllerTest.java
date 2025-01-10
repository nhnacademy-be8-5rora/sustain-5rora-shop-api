package store.aurora.point.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.aurora.point.entity.PointPolicy;
import store.aurora.point.entity.PointPolicyType;
import store.aurora.point.service.PointPolicyService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PointPolicyControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private PointPolicyController pointPolicyController;

    @Mock
    private PointPolicyService pointPolicyService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pointPolicyController).build();
    }

    @Test
    @DisplayName("GET /api/points/policies - Should return all point policies")
    void testGetAllPointPolicies() throws Exception {
        // Given
        PointPolicy policy1 = new PointPolicy(1, "Policy1", PointPolicyType.PERCENTAGE, BigDecimal.valueOf(10.0));
        PointPolicy policy2 = new PointPolicy(2, "Policy2", PointPolicyType.PERCENTAGE, BigDecimal.valueOf(5.0));
        when(pointPolicyService.getAllPointPolicies()).thenReturn(List.of(policy1, policy2));

        // When / Then
        mockMvc.perform(get("/api/points/policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pointPolicyName").value("Policy1"))
                .andExpect(jsonPath("$[1].pointPolicyName").value("Policy2"));

        verify(pointPolicyService, times(1)).getAllPointPolicies();
    }

    @Test
    @DisplayName("PATCH /api/points/policies/{id} - Should update point policy value")
    void testUpdatePointPolicyValue() throws Exception {
        // Given
        Integer policyId = 1;
        String requestBody = "{\"pointPolicyValue\":20.0}";

        // When / Then
        mockMvc.perform(patch("/api/points/policies/{id}", policyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(pointPolicyService, times(1)).updatePointPolicyValue(policyId, BigDecimal.valueOf(20.0));
    }

    @Test
    @DisplayName("PATCH /api/points/policies/{id}/toggle-status - Should toggle policy status")
    void testToggleStatus() throws Exception {
        // Given
        Integer policyId = 1;

        // When / Then
        mockMvc.perform(patch("/api/points/policies/{id}/toggle-status", policyId))
                .andExpect(status().isOk());

        verify(pointPolicyService, times(1)).toggleStatus(policyId);
    }

    @Test
    @DisplayName("POST /api/points/policies - Should create a new point policy")
    void testCreatePointPolicy() throws Exception {
        // Given
        PointPolicy policy = new PointPolicy(1, "Policy1", PointPolicyType.PERCENTAGE, BigDecimal.valueOf(10.0));
        when(pointPolicyService.createPointPolicy(any(PointPolicy.class))).thenReturn(policy);
        String requestBody = "{\"pointPolicyId\":\"1\",\"pointPolicyName\":\"Policy1\",\"pointPolicyType\":\"PERCENTAGE\",\"pointPolicyValue\":10.0}";

        // When / Then
        mockMvc.perform(post("/api/points/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pointPolicyName").value("Policy1"));

        verify(pointPolicyService, times(1)).createPointPolicy(any(PointPolicy.class));
    }
}
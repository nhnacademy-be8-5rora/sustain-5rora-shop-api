package store.aurora.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import store.aurora.user.entity.Rank;
import store.aurora.user.entity.UserRank;
import store.aurora.user.service.impl.UserRankService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserRankController.class)
class UserRankControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRankService userRankService;

    @Test
    void testGetAllUserRanks() throws Exception {
        // Arrange
        UserRank rank1 = new UserRank(1L, Rank.GENERAL, 0, 1000, BigDecimal.valueOf(0.01));
        UserRank rank2 = new UserRank(2L, Rank.GOLD, 1001, 5000, BigDecimal.valueOf(0.02));
        List<UserRank> mockRanks = Arrays.asList(rank1, rank2);

        when(userRankService.getAllUserRanks()).thenReturn(mockRanks);

        // Act & Assert
        mockMvc.perform(get("/api/user-ranks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))

                .andExpect(jsonPath("$[0].rankName").value(Rank.GENERAL.name()))
                .andExpect(jsonPath("$[0].minAmount").value(0))
                .andExpect(jsonPath("$[0].maxAmount").value(1000))
                .andExpect(jsonPath("$[0].pointRate").value(0.01))

                .andExpect(jsonPath("$[1].rankName").value(Rank.GOLD.name()))
                .andExpect(jsonPath("$[1].minAmount").value(1001))
                .andExpect(jsonPath("$[1].maxAmount").value(5000))
                .andExpect(jsonPath("$[1].pointRate").value(0.02));
    }
}
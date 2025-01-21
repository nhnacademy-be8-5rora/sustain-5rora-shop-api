package store.aurora.user.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import store.aurora.order.entity.Order;
import store.aurora.order.repository.OrderDetailRepository;
import store.aurora.user.entity.Rank;
import store.aurora.user.entity.User;
import store.aurora.user.entity.UserRank;
import store.aurora.user.entity.UserRankHistory;
import store.aurora.user.repository.UserRankHistoryRepository;
import store.aurora.user.repository.UserRankRepository;
import store.aurora.user.repository.UserRepository;
import store.aurora.user.service.impl.UserRankService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;


class UserRankServiceTest {

    @InjectMocks
    private UserRankService userRankService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRankRepository userRankRepository;

    @Mock
    private UserRankHistoryRepository userRankHistoryRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private User user;

    @Mock
    private Order order;

    @Mock
    private UserRank userRank;

    @Mock
    private UserRankHistory userRankHistory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCurrentPointRateByUserId() {
        String userId = "user123";
        BigDecimal expectedPointRate = BigDecimal.valueOf(1.2);

        when(userRankHistoryRepository.findTopByUserIdOrderByChangedAtDesc(userId))
                .thenReturn(Optional.of(userRankHistory));
        when(userRankHistory.getUserRank()).thenReturn(userRank);
        when(userRank.getPointRate()).thenReturn(expectedPointRate);

        Optional<BigDecimal> pointRate = userRankService.getCurrentPointRateByUserId(userId);

        assertTrue(pointRate.isPresent());
        assertEquals(expectedPointRate, pointRate.get());
    }

//    @Test
//    void testUpdateUserRankBasedOnOrder() {
//        String userId = "user123";
//        Integer pureAmount = 1000;
//        UserRank newRank = new UserRank();
//        newRank.setMinAmount(500);
//        newRank.setMaxAmount(1500);
//
//        when(order.getUser()).thenReturn(user);
//        when(userRankRepository.findAll()).thenReturn(List.of(newRank));
//
//        // Mock the calculation of pureAmount
//        when(userRankService.calculatePureAmountForLastThreeMonths(user)).thenReturn(pureAmount);
//
//        userRankService.updateUserRankBasedOnOrder(order);
//
//        // Verify if the UserRankHistory repository is called to save the new rank
//        verify(userRankHistoryRepository, times(1)).save(any(UserRankHistory.class));
//    }
//
//    @Test
//    void testCalculatePureAmountForLastThreeMonths() {
//        Integer orderAmount = 1000;
//        Integer wrapCost = 100;
//        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
//        User user = new User();
//
//        // Mock order and order details
//        when(user.getOrders()).thenReturn(List.of(order));
//        when(order.getOrderTime()).thenReturn(threeMonthsAgo.plusDays(1));
//        when(order.getOrderDetails()).thenReturn(List.of());
//        when(orderDetailRepository.calculateTotalWrapCostByOrderId(any())).thenReturn(wrapCost);
//
//        // Simulate the calculation of pure amount
//        Integer pureAmount = userRankService.calculatePureAmountForLastThreeMonths(user);
//
//        // Assert the value of pure amount
//        assertEquals(orderAmount - wrapCost, pureAmount);
//    }
//
//    @Test
//    void testGetRankBasedOnAmount() {
//        Integer pureAmount = 1000;
//        UserRank rank = new UserRank();
//        rank.setMinAmount(500);
//        rank.setMaxAmount(1500);
//
//        when(userRankRepository.findAll()).thenReturn(List.of(rank));
//
//        UserRank resultRank = userRankService.getRankBasedOnAmount(pureAmount);
//
//        assertNotNull(resultRank);
//        assertEquals(rank, resultRank);
//    }

    @Test
    void testGetAllUserRanks() {
        // Arrange
        UserRank rank1 = new UserRank(1L, Rank.GENERAL, 0, 1000, BigDecimal.valueOf(0.01));
        UserRank rank2 = new UserRank(2L, Rank.PLATINUM, 1001, 5000, BigDecimal.valueOf(0.02));
        List<UserRank> mockRanks = Arrays.asList(rank1, rank2);

        when(userRankRepository.findAll()).thenReturn(mockRanks);

        // Act
        List<UserRank> result = userRankService.getAllUserRanks();

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(2);

        assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("rankName", Rank.GENERAL)
                .hasFieldOrPropertyWithValue("minAmount", 0)
                .hasFieldOrPropertyWithValue("maxAmount", 1000)
                .hasFieldOrPropertyWithValue("pointRate", BigDecimal.valueOf(0.01));

        assertThat(result.get(1))
                .hasFieldOrPropertyWithValue("rankName", Rank.PLATINUM)
                .hasFieldOrPropertyWithValue("minAmount", 1001)
                .hasFieldOrPropertyWithValue("maxAmount", 5000)
                .hasFieldOrPropertyWithValue("pointRate", BigDecimal.valueOf(0.02));

        verify(userRankRepository, times(1)).findAll();
    }
}

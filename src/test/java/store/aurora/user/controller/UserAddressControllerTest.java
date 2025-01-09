package store.aurora.user.controller;

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
import store.aurora.user.entity.Address;
import store.aurora.user.entity.User;
import store.aurora.user.entity.UserAddress;
import store.aurora.user.service.impl.UserAddressService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserAddressControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserAddressController userAddressController;

    @Mock
    private UserAddressService userAddressService;

    private final String userId = "user1";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userAddressController).build();
    }

    @Test
    @DisplayName("GET /api/addresses - Should return user addresses")
    void testGetUserAddresses() throws Exception {
        // Given
        List<UserAddress> mockAddresses = List.of(
                new UserAddress("Home", "123 Main St", new Address("123 Main St"), "John Doe", new User(userId, "John", null, "010-1234-5678", "test@example.com", false))
        );
        when(userAddressService.getUserAddresses(userId)).thenReturn(mockAddresses);


        // When / Then
        mockMvc.perform(get("/api/addresses")
                        .header("X-USER-ID", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nickname").value("Home"))
                .andExpect(jsonPath("$[0].addrDetail").value("123 Main St"))
                .andExpect(jsonPath("$[0].receiver").value("John Doe"));

        verify(userAddressService, times(1)).getUserAddresses(userId);
    }

    @Test
    @DisplayName("GET /api/addresses/{id} - Should return user address")
    void testGetUserAddress() throws Exception {
        // Given
        Long userAddressId = 1L;
        UserAddress mockUserAddress = new UserAddress("Home", "123 Main St", new Address("123 Main St"), "John Doe", new User(userId, "John", null, "010-1234-5678", "test@example.com", false));
        when(userAddressService.getUserAddressByIdAndUserId(userAddressId, userId)).thenReturn(mockUserAddress);

        // When / Then
        mockMvc.perform(get("/api/addresses/{id}", userAddressId)
                        .header("X-USER-ID", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("Home"))
                .andExpect(jsonPath("$.addrDetail").value("123 Main St"))
                .andExpect(jsonPath("$.receiver").value("John Doe"));

        verify(userAddressService, times(1)).getUserAddressByIdAndUserId(userAddressId, userId);
    }

    @Test
    @DisplayName("POST /api/addresses - Should add user address")
    void testAddUserAddress() throws Exception {
        // Given
        String requestBody = "{\"nickname\":\"Home\",\"receiver\":\"John Doe\",\"roadAddress\":\"123 Main St\",\"addrDetail\":\"Apartment 456\"}";

        // When / Then
        mockMvc.perform(post("/api/addresses")
                        .header("X-USER-ID", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(userAddressService, times(1)).addUserAddress(anyString(), anyString(), anyString(), anyString(), eq(userId));
    }

    @Test
    @DisplayName("PUT /api/addresses/{id} - Should update user address")
    void testUpdateUserAddress() throws Exception {
        // Given
        Long userAddressId = 1L;
        String requestBody = "{\"nickname\":\"Home\",\"receiver\":\"John Doe\",\"roadAddress\":\"123 Main St\",\"addrDetail\":\"Apartment 456\"}";

        // When / Then
        mockMvc.perform(put("/api/addresses/{id}", userAddressId)
                        .header("X-USER-ID", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(userAddressService, times(1)).updateUserAddress(eq(userAddressId), anyString(), anyString(), anyString(), eq(userId));
    }

    @Test
    @DisplayName("DELETE /api/addresses/{id} - Should delete user address")
    void testDeleteUserAddress() throws Exception {
        // Given
        Long userAddressId = 1L;

        // When / Then
        mockMvc.perform(delete("/api/addresses/{id}", userAddressId)
                        .header("X-USER-ID", userId))
                .andExpect(status().isOk());

        verify(userAddressService, times(1)).deleteUserAddress(userAddressId, userId);
    }
}
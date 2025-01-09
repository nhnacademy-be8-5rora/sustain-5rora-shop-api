package store.aurora.user.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import store.aurora.user.entity.Address;
import store.aurora.user.entity.User;
import store.aurora.user.entity.UserAddress;
import store.aurora.user.exception.AddressLimitExceededException;
import store.aurora.user.exception.UserAddressAlreadyExistsException;
import store.aurora.user.exception.UserAddressNotFoundException;
import store.aurora.user.repository.UserAddressRepository;
import store.aurora.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAddressServiceTest {

    @InjectMocks
    private UserAddressService userAddressService;

    @Mock
    private UserAddressRepository userAddressRepository;

    @Mock
    private AddressService addressService;

    @Mock
    private UserService userService;

    @Test
    @DisplayName("addUserAddress: Should add a new UserAddress if conditions are met")
    void testAddUserAddress_Success() {
        // Given
        String userId = "user1";
        String nickname = "Home";
        String receiver = "John Doe";
        String roadAddress = "123 Test Street";
        String addrDetail = "Apartment 456";

        User mockUser = new User(userId, "John", null, "010-1234-5678", "test@example.com", false);
        Address mockAddress = new Address(roadAddress);
        UserAddress newUserAddress = new UserAddress(nickname, addrDetail, mockAddress, receiver, mockUser);

        when(userAddressRepository.countByUserId(userId)).thenReturn(5);
        when(addressService.saveOrGetAddress(roadAddress)).thenReturn(mockAddress);
        when(userAddressRepository.findByUserIdAndAddressIdAndAddrDetailAndReceiver(userId, mockAddress.getId(), addrDetail, receiver)).thenReturn(Optional.empty());
        when(userService.getUser(userId)).thenReturn(mockUser);
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(newUserAddress);

        // When
        userAddressService.addUserAddress(nickname, receiver, roadAddress, addrDetail, userId);

        // Then
        verify(userAddressRepository, times(1)).countByUserId(userId);
        verify(addressService, times(1)).saveOrGetAddress(roadAddress);
        verify(userAddressRepository, times(1)).findByUserIdAndAddressIdAndAddrDetailAndReceiver(userId, mockAddress.getId(), addrDetail, receiver);
        verify(userAddressRepository, times(1)).save(any(UserAddress.class));
    }

    @Test
    @DisplayName("addUserAddress: Should throw exception when address limit is exceeded")
    void testAddUserAddress_LimitExceeded() {
        // Given
        String userId = "user1";

        when(userAddressRepository.countByUserId(userId)).thenReturn(10);

        // When / Then
        assertThatThrownBy(() -> userAddressService.addUserAddress("Home", "John Doe", "123 Test Street", "Apartment 456", userId))
                .isInstanceOf(AddressLimitExceededException.class);

        verify(userAddressRepository, times(1)).countByUserId(userId);
        verifyNoMoreInteractions(userAddressRepository);
    }

    @Test
    @DisplayName("addUserAddress: Should throw UserAddressAlreadyExistsException if address already exists")
    void testAddUserAddress_AlreadyExists() {
        // Given
        String userId = "user1";
        String nickname = "Home";
        String receiver = "John Doe";
        String roadAddress = "123 Test Street";
        String addrDetail = "Apartment 456";

        Address mockAddress = new Address(roadAddress);
        when(userAddressRepository.countByUserId(userId)).thenReturn(5);
        when(addressService.saveOrGetAddress(roadAddress)).thenReturn(mockAddress);
        when(userAddressRepository.findByUserIdAndAddressIdAndAddrDetailAndReceiver(userId, mockAddress.getId(), addrDetail, receiver))
                .thenReturn(Optional.of(new UserAddress(nickname, addrDetail, mockAddress, receiver, new User(userId, "John", null, "010-1234-5678", "test@example.com", false))));

        // When / Then
        assertThatThrownBy(() -> userAddressService.addUserAddress(nickname, receiver, roadAddress, addrDetail, userId))
                .isInstanceOf(UserAddressAlreadyExistsException.class)
                .hasMessage("중복된 배송지가 이미 존재합니다.");

        verify(userAddressRepository, times(1)).countByUserId(userId);
        verify(addressService, times(1)).saveOrGetAddress(roadAddress);
        verify(userAddressRepository, times(1)).findByUserIdAndAddressIdAndAddrDetailAndReceiver(userId, mockAddress.getId(), addrDetail, receiver);
        verify(userAddressRepository, times(0)).save(any(UserAddress.class));
    }

    @Test
    @DisplayName("getUserAddresses: Should return list of UserAddresses")
    void testGetUserAddresses() {
        // Given
        String userId = "user1";
        List<UserAddress> mockAddresses = List.of(
                new UserAddress("Home", "123 Test St", new Address("123 Test St"), "John Doe", new User(userId, "John", null, "010-1234-5678", "test@example.com", false))
        );

        when(userAddressRepository.findByUserId(userId)).thenReturn(mockAddresses);

        // When
        List<UserAddress> result = userAddressService.getUserAddresses(userId);

        // Then
        assertThat(result)
                .isNotNull()
                .hasSize(1);
        verify(userAddressRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("updateUserAddress: Should update UserAddress fields successfully")
    void testUpdateUserAddress_Success() {
        // Given
        Long userAddressId = 1L;
        String userId = "user1";
        String newReceiver = "Jane Doe";
        String newAddrDetail = "New Apartment 789";
        String newRoadAddress = "456 Updated Street";

        Address oldAddress = new Address("123 Old Street");
        Address newAddress = new Address(newRoadAddress);
        User mockUser = new User(userId, "John", null, "010-1234-5678", "test@example.com", false);
        UserAddress mockUserAddress = new UserAddress("Home", "Old Detail", oldAddress, "John Doe", mockUser);

        when(userAddressRepository.findByIdAndUserId(userAddressId, userId)).thenReturn(Optional.of(mockUserAddress));
        when(addressService.saveOrGetAddress(newRoadAddress)).thenReturn(newAddress);

        // When
        userAddressService.updateUserAddress(userAddressId, newReceiver, newAddrDetail, newRoadAddress, userId);

        // Then
        assertThat(mockUserAddress.getReceiver()).isEqualTo(newReceiver);
        assertThat(mockUserAddress.getAddrDetail()).isEqualTo(newAddrDetail);
        assertThat(mockUserAddress.getAddress()).isEqualTo(newAddress);

        verify(userAddressRepository, times(1)).findByIdAndUserId(userAddressId, userId);
        verify(addressService, times(1)).saveOrGetAddress(newRoadAddress);
    }

    @Test
    @DisplayName("updateUserAddress: Should throw exception if UserAddress not found")
    void testUpdateUserAddress_NotFound() {
        // Given
        Long userAddressId = 1L;
        String userId = "user1";

        when(userAddressRepository.findByIdAndUserId(userAddressId, userId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> userAddressService.updateUserAddress(userAddressId, "Jane Doe", "New Detail", "456 Updated Street", userId))
                .isInstanceOf(UserAddressNotFoundException.class);

        verify(userAddressRepository, times(1)).findByIdAndUserId(userAddressId, userId);
        verify(addressService, times(0)).saveOrGetAddress(any());
    }

    @Test
    @DisplayName("deleteUserAddress: Should delete a UserAddress if it exists")
    void testDeleteUserAddress() {
        // Given
        String userId = "user1";
        Long userAddressId = 1L;
        UserAddress mockUserAddress = new UserAddress("Home", "123 Test St", new Address("123 Test St"), "John Doe", new User(userId, "John", null, "010-1234-5678", "test@example.com", false));

        when(userAddressRepository.findByIdAndUserId(userAddressId, userId)).thenReturn(Optional.of(mockUserAddress));

        // When
        userAddressService.deleteUserAddress(userAddressId, userId);

        // Then
        verify(userAddressRepository, times(1)).findByIdAndUserId(userAddressId, userId);
        verify(userAddressRepository, times(1)).delete(mockUserAddress);
    }

    @Test
    @DisplayName("deleteUserAddress: Should throw exception if UserAddress not found")
    void testDeleteUserAddress_NotFound() {
        // Given
        String userId = "user1";
        Long userAddressId = 1L;

        when(userAddressRepository.findByIdAndUserId(userAddressId, userId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> userAddressService.deleteUserAddress(userAddressId, userId))
                .isInstanceOf(UserAddressNotFoundException.class);

        verify(userAddressRepository, times(1)).findByIdAndUserId(userAddressId, userId);
        verify(userAddressRepository, times(0)).delete(any(UserAddress.class));
    }
}
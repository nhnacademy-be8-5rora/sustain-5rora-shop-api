package store.aurora.user.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import store.aurora.user.entity.Address;
import store.aurora.user.repository.AddressRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    private AddressService addressService;

    @Mock
    private AddressRepository addressRepository;

    @Test
    @DisplayName("saveOrGetAddress: Should save a new Address if not exists")
    void testSaveOrGetAddress_SaveNew() {
        // Given
        String roadAddress = "123 Test Street";
        Address newAddress = new Address(roadAddress);

        when(addressRepository.findByRoadAddress(roadAddress)).thenReturn(Optional.empty());
        when(addressRepository.save(any(Address.class))).thenReturn(newAddress);

        // When
        Address address = addressService.saveOrGetAddress(roadAddress);

        // Then
        assertThat(address).isNotNull();
        assertThat(address.getRoadAddress()).isEqualTo(roadAddress);

        verify(addressRepository, times(1)).findByRoadAddress(roadAddress);
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("saveOrGetAddress: Should return existing Address if exists")
    void testSaveOrGetAddress_ReturnExisting() {
        // Given
        String roadAddress = "123 Test Street";
        Address existingAddress = new Address(roadAddress);

        when(addressRepository.findByRoadAddress(roadAddress)).thenReturn(Optional.of(existingAddress));

        // When
        Address address = addressService.saveOrGetAddress(roadAddress);

        // Then
        assertThat(address).isNotNull();
        assertThat(address.getRoadAddress()).isEqualTo(existingAddress.getRoadAddress());

        verify(addressRepository, times(1)).findByRoadAddress(roadAddress);
        verify(addressRepository, times(0)).save(any(Address.class));
    }
}
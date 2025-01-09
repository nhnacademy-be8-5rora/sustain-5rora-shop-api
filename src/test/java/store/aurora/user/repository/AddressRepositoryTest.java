package store.aurora.user.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.user.entity.Address;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfiguration.class)
class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Test
    @DisplayName("findByRoadAddress: Should return Address when roadAddress exists")
    void testFindByRoadAddress_Exists() {
        // Given
        Address address = new Address("123 Test Road");
        addressRepository.save(address);

        // When
        Optional<Address> result = addressRepository.findByRoadAddress("123 Test Road");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getRoadAddress()).isEqualTo("123 Test Road");
    }

    @Test
    @DisplayName("findByRoadAddress: Should return empty when roadAddress does not exist")
    void testFindByRoadAddress_NotExists() {
        // When
        Optional<Address> result = addressRepository.findByRoadAddress("Nonexistent Road");

        // Then
        assertThat(result).isNotPresent();
    }
}
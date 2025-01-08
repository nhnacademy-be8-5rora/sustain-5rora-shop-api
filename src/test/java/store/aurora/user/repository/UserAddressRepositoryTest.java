package store.aurora.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.user.entity.Address;
import store.aurora.user.entity.User;
import store.aurora.user.entity.UserAddress;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfiguration.class)
class UserAddressRepositoryTest {

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Address address;
    private UserAddress userAddress1;

    @BeforeEach
    public void setup() {
        user = new User("user1", "Test User", LocalDate.of(1990, 1, 1), "010-1234-5678", "test@example.com", false);
        entityManager.persist(user);

        address = new Address("123 Test Road"); // Assume Address object is properly initialized
        entityManager.persist(address);

        userAddress1 = new UserAddress("Home", "123 Main St", address, "John Doe", user);
        userAddressRepository.save(userAddress1);
    }


    @Test
    @DisplayName("countByUserId: Should return the count of addresses for a given userId")
    void testCountByUserId() {
        // Given
        UserAddress userAddress2 = new UserAddress("Work", "456 Work Rd", address, "John Doe", user);
        userAddressRepository.save(userAddress2);

        // When
        int count = userAddressRepository.countByUserId(user.getId());

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("findByUserIdAndAddressIdAndAddrDetailAndReceiver: Should find specific address")
    void testFindByUserIdAndAddressIdAndAddrDetailAndReceiver() {
        // When
        Optional<UserAddress> result = userAddressRepository.findByUserIdAndAddressIdAndAddrDetailAndReceiver(
                user.getId(), address.getId(), "123 Main St", "John Doe");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getAddrDetail()).isEqualTo("123 Main St");
    }

    @Test
    @DisplayName("findByUserId: Should return all addresses for a user")
    void testFindByUserId() {
        // Given
        UserAddress userAddress2 = new UserAddress("Work", "456 Work Rd", address, "John Doe", user);
        userAddressRepository.save(userAddress2);

        // When
        List<UserAddress> result = userAddressRepository.findByUserId(user.getId());

        // Then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("findByIdAndUserId: Should return address by id and userId")
    void testFindByIdAndUserId() {
        // When
        Optional<UserAddress> result = userAddressRepository.findByIdAndUserId(userAddress1.getId(), user.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getNickname()).isEqualTo("Home");
    }
}
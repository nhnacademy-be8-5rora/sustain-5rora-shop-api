package store.aurora.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.aurora.user.entity.UserAddress;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
}

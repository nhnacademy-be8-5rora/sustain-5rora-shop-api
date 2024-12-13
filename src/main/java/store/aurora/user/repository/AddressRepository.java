package store.aurora.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.aurora.user.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}

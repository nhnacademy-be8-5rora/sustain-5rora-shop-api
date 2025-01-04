package store.aurora.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.aurora.user.entity.Address;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    // 도로명 주소로 Address를 찾는 메서드
    Optional<Address> findByRoadAddress(String roadAddress);
}

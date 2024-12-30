package store.aurora.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import store.aurora.user.entity.UserAddress;

import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    @Query("SELECT ua FROM UserAddress ua WHERE ua.user.id = :userId AND ua.address.id = :addressId AND ua.addrDetail = :addrDetail AND ua.receiver = :receiver")
    Optional<UserAddress> findDuplicateAddress(@Param("userId") String userId,
                                               @Param("addressId") Long addressId,
                                               @Param("addrDetail") String addrDetail,
                                               @Param("receiver") String receiver);
}

package store.aurora.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.common.exception.DataAlreadyExistsException;
import store.aurora.user.entity.Address;
import store.aurora.user.entity.User;
import store.aurora.user.entity.UserAddress;
import store.aurora.user.exception.UserAddressAlreadyExistsException;
import store.aurora.user.repository.UserAddressRepository;
import store.aurora.user.service.UserService;

@Service
@RequiredArgsConstructor
public class UserAddressService {
    private final UserAddressRepository userAddressRepository;
    private final AddressService addressService;
    private final UserService userService;

    @Transactional
    public void addUserAddress(String receiver, String roadAddress, String addrDetail, String userId) {
        // Address 엔터티 저장 또는 가져오기
        Address address = addressService.saveOrGetAddress(roadAddress);

        if (userAddressRepository.findByUserIdAndAddressIdAndAddrDetailAndReceiver(
                userId,
                address.getId(),
                addrDetail,
                receiver
        ).isPresent()) {
            throw new UserAddressAlreadyExistsException("중복된 배송지가 이미 존재합니다.");
        }

        userAddressRepository.save(
                new UserAddress(addrDetail, address, receiver, userService.getUser(userId)));
    }
}
package store.aurora.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.common.exception.DataAlreadyExistsException;
import store.aurora.user.entity.Address;
import store.aurora.user.entity.User;
import store.aurora.user.entity.UserAddress;
import store.aurora.user.exception.UserAddressAlreadyExistsException;
import store.aurora.user.exception.UserAddressNotFoundException;
import store.aurora.user.repository.UserAddressRepository;
import store.aurora.user.service.UserService;

import java.util.List;

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

    public List<UserAddress> getUserAddresses(String userId) {
        return userAddressRepository.findByUserId(userId);
    }

    @Transactional
    public void updateUserAddress(Long userAddressId, String receiver, String addrDetail, String roadAddress, String userId) {
        UserAddress userAddress = getUserAddressByIdAndUserId(userAddressId, userId);

        // Address 수정: 도로명 주소가 변경되었을 경우 새로운 Address 생성 또는 조회
        Address address = addressService.saveOrGetAddress(roadAddress);

        // UserAddress 필드 업데이트
        userAddress.setReceiver(receiver);
        userAddress.setAddrDetail(addrDetail);
        userAddress.setAddress(address);
    }

    @Transactional
    public void deleteUserAddress(Long userAddressId, String userId) {
        UserAddress userAddress = getUserAddressByIdAndUserId(userAddressId, userId);
        userAddressRepository.delete(userAddress);
    }

    // 특정 사용자의 배송지 조회
    public UserAddress getUserAddressByIdAndUserId(Long userAddressId, String userId) {
        return userAddressRepository.findByIdAndUserId(userAddressId, userId)
                .orElseThrow(() -> new UserAddressNotFoundException(userAddressId));
    }
}
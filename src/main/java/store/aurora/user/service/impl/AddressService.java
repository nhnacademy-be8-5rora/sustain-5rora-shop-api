package store.aurora.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.user.entity.Address;
import store.aurora.user.repository.AddressRepository;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    // Address 엔터티를 저장하거나 기존 Address 반환
    @Transactional
    public Address saveOrGetAddress(String roadAddress) {
        return addressRepository.findByRoadAddress(roadAddress)
                .orElseGet(() -> addressRepository.save(new Address(roadAddress)));
    }
}
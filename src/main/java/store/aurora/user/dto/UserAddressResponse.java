package store.aurora.user.dto;

import lombok.Builder;
import lombok.Data;
import store.aurora.user.entity.UserAddress;

@Data
@Builder
public class UserAddressResponse {
    private long id;
    private String receiver;       // 수취인 이름
    private String roadAddress;    // 도로명 주소
    private String addrDetail;     // 상세주소

    public static UserAddressResponse fromEntity(UserAddress userAddress) {
        return UserAddressResponse.builder()
                .id(userAddress.getId())
                .receiver(userAddress.getReceiver())
                .roadAddress(userAddress.getAddress().getRoadAddress())
                .addrDetail(userAddress.getAddrDetail())
                .build();
    }
}
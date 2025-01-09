package store.aurora.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserAddressRequest {
    @NotBlank
    private String nickname;
    @NotBlank
    private String receiver;     // 수취인
    @NotBlank
    private String roadAddress;  // 도로명 주소
    @NotBlank
    private String addrDetail;   // 상세주소
}
package store.aurora.user.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerificationRequest {
    private String phoneNumber;
    private String verificationCode;
}

package store.aurora.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignUpRequest {
    @NotBlank(message = "아이디는 필수 항목입니다.")
    private String id;

//    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자리 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수 항목입니다.")
    private String name;

    @NotBlank(message = "생년월일은 필수 항목입니다.")
    @JsonFormat(pattern = "yyyyMMdd")
    private String birth;

    @NotBlank(message = "전화번호는 필수 항목입니다.")
    @Pattern(regexp = "^[0-9]{10,12}$", message = "전화번호는 숫자만 입력해주세요.")
    private String phoneNumber;

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "올바른 이메일 형식을 입력하세요.")
    private String email;

    private String verificationCode;    // 인증번호 입력

}



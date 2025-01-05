package store.aurora.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ValidationErrorResponse {
    @Schema(description = "에러 메시지 목록", example = "[\"pointPolicyName은(는) 공백일 수 없습니다.\"]")
    private List<String> errors;
}
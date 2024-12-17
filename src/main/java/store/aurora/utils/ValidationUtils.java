package store.aurora.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Objects;

public class ValidationUtils {

    // 입력 값 검증
    public static void validateStringInput(String input, String errorMessage) {
        if (Objects.isNull(input) || input.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    // 입력 값이 유효하지 않을 때 빈 페이지 반환
    public static <T> Page<T> emptyPage(Pageable pageable) {
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }
}

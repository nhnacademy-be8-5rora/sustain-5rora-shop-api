package store.aurora.coupon;

import org.junit.jupiter.api.Test;
import store.aurora.coupon.dto.ProductInfoDTO;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

class ProductInfoDTOTest {

    @Test
    void testProductInfoDTO() {
        // ProductInfoDTO 객체 생성
        ProductInfoDTO productInfo = new ProductInfoDTO();

        // Setter를 사용하여 값 설정
        productInfo.setBookId(100L);
        productInfo.setCategoryIds(Arrays.asList(1L, 2L, 3L));
        productInfo.setPrice(20000);

        // Getter를 사용하여 값 확인
        assertEquals(100L, productInfo.getBookId());
        assertEquals(Arrays.asList(1L, 2L, 3L), productInfo.getCategoryIds());
        assertEquals(20000, productInfo.getPrice());
    }
}
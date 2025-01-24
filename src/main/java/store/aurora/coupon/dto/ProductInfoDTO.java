package store.aurora.coupon.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ProductInfoDTO {
    @NotNull private Long bookId;               //상품의 고유 ID
    @NotNull private List<Long> categoryIds;    //상품이 속해있는 카테고리 Id
    @NotNull private Integer price;             //상품의 가격
}

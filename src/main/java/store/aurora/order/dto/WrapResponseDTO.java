package store.aurora.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WrapResponseDTO {
    private Long id;
    private String name;
    private Integer amount;
}

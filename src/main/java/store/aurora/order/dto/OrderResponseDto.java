package store.aurora.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class OrderResponseDto {
    @JsonProperty("customer_key")
    String customerKey;

    @JsonProperty("currency")
    String currency;

    @JsonProperty("value")
    Integer value;

    @JsonProperty("order_name")
    String orderName;
}

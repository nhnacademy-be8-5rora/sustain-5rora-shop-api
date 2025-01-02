package store.aurora.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class OrderDTO {
    private LocalDate preferredDeliveryDate;
    private LocalDateTime orderTime;
    private Integer pointAmount;
}

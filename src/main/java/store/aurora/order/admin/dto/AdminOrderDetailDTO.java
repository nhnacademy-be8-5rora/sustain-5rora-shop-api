package store.aurora.order.admin.dto;

public record AdminOrderDetailDTO(
        Long OrderDetailId,
        String shipmentState,
        String shipmentDate
) {
}

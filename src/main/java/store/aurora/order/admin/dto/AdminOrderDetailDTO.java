package store.aurora.order.admin.dto;

public record AdminOrderDetailDTO(
        Long orderDetailId,
        String shipmentState,
        String shipmentDate
) {
}

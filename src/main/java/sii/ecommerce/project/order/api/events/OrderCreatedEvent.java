package sii.ecommerce.project.order.api.events;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID clientId,
        BigDecimal totalAmount,
        List<OrderItemPayload> items
) {

}
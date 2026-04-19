package sii.ecommerce.project.order.api.events;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemPayload(UUID productId, int quantity, BigDecimal snapshotPrice) {
}
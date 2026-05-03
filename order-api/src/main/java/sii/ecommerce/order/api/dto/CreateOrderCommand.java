package sii.ecommerce.order.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
        @NotNull(message = "Client id cannot be empty")
        UUID clientId,

        @NotEmpty(message = "Cart cannot be empty")
        @Valid
        List<OrderItemDto> items
) {}

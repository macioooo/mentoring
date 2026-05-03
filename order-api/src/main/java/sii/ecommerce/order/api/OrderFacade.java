package sii.ecommerce.order.api;

import sii.ecommerce.order.api.dto.CreateOrderCommand;

import java.util.UUID;

public interface OrderFacade {

    UUID createOrder(CreateOrderCommand createOrderCommand);
}

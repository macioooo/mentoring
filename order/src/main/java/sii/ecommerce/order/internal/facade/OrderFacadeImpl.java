package sii.ecommerce.order.internal.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sii.ecommerce.order.api.OrderFacade;
import sii.ecommerce.order.api.dto.CreateOrderCommand;
import sii.ecommerce.order.internal.service.OrderService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class OrderFacadeImpl implements OrderFacade {

    private final OrderService orderService;

    @Override
    public UUID createOrder(CreateOrderCommand createOrderCommand) {
        return orderService.createOrder(createOrderCommand);
    }
}

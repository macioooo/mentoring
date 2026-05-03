package sii.ecommerce.app.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sii.ecommerce.order.api.OrderFacade;
import sii.ecommerce.order.api.dto.CreateOrderCommand;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderCommand createOrderCommand) {
        UUID orderId = orderFacade.createOrder(createOrderCommand);
        return ResponseEntity.status(HttpStatus.CREATED).body(new OrderResponse(orderId));
    }

    public record OrderResponse(UUID orderId) {}
}

package sii.ecommerce.project.order.internal.web;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sii.ecommerce.project.order.api.dto.CreateOrderCommand;
import sii.ecommerce.project.order.internal.service.OrderService;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderCommand command) {
        UUID orderId = orderService.createOrder(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(new OrderResponse(orderId));
    }

    public record OrderResponse(UUID orderId) {}
}
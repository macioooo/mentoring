package sii.ecommerce.project.order.api.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sii.ecommerce.project.order.api.dto.CreateOrderCommand;
import sii.ecommerce.project.order.internal.service.OrderService;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody CreateOrderCommand command) {
        UUID orderId = orderService.createOrder(command);

        return ResponseEntity.ok("Order " + orderId + " was created properl!");
    }
}

package sii.ecommerce.order.internal.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sii.ecommerce.catalog.api.ProductCatalogFacade;
import sii.ecommerce.order.api.dto.CreateOrderCommand;
import sii.ecommerce.order.api.dto.OrderItemDto;
import sii.ecommerce.order.api.events.OrderCreatedEvent;
import sii.ecommerce.order.api.events.OrderItemPayload;
import sii.ecommerce.order.internal.model.Order;
import sii.ecommerce.order.internal.model.OutboxMessage;
import sii.ecommerce.order.internal.repository.OrderRepository;
import sii.ecommerce.order.internal.repository.OutboxRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ProductCatalogFacade productCatalogFacade;
    private final ObjectMapper objectMapper;

    @Transactional
    public UUID createOrder(CreateOrderCommand createOrderCommand) {
        log.info("Starting processing order for client: {}", createOrderCommand.clientId());

        Order order = new Order(createOrderCommand.clientId());

        for (OrderItemDto cartLine : createOrderCommand.items()) {
            ProductCatalogFacade.ProductSnapshot productSnapshot =
                    productCatalogFacade.fetchProductDetails(cartLine.productId());
            order.addItem(
                    cartLine.productId(), productSnapshot.name(), productSnapshot.price(), cartLine.quantity());
        }

        orderRepository.save(order);

        List<OrderItemPayload> orderCreatedEventLines = order.getItems().stream()
                .map(orderItem -> new OrderItemPayload(
                        orderItem.getProductId(), orderItem.getQuantity(), orderItem.getSnapshotPrice()))
                .toList();

        OrderCreatedEvent orderCreatedEvent =
                new OrderCreatedEvent(order.getId(), order.getClientId(), order.getTotalAmount(), orderCreatedEventLines);

        String serializedOrderCreatedEvent;
        try {
            serializedOrderCreatedEvent = objectMapper.writeValueAsString(orderCreatedEvent);
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("Error while event serialization for order {}", order.getId(), jsonProcessingException);
            throw new RuntimeException("System error while processing the order", jsonProcessingException);
        }

        OutboxMessage outboxMessage = OutboxMessage.builder()
                .aggregateType("ORDER")
                .aggregateId(order.getId().toString())
                .eventType("OrderCreated")
                .payload(serializedOrderCreatedEvent)
                .build();

        outboxRepository.save(outboxMessage);
        log.info("Saved the order {} and Outbox event", order.getId());

        return order.getId();
    }
}
